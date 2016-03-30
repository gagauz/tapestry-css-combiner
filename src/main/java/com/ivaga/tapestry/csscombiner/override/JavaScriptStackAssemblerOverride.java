package com.ivaga.tapestry.csscombiner.override;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.assets.BytestreamCache;
import org.apache.tapestry5.internal.services.assets.CompressedStreamableResource;
import org.apache.tapestry5.internal.services.assets.JavaScriptStackAssembler;
import org.apache.tapestry5.internal.services.assets.ResourceChangeTracker;
import org.apache.tapestry5.internal.services.assets.StreamableResourceImpl;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.assets.AssetChecksumGenerator;
import org.apache.tapestry5.services.assets.CompressionStatus;
import org.apache.tapestry5.services.assets.ResourceMinimizer;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.apache.tapestry5.services.assets.StreamableResourceProcessing;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.JavaScriptAggregationStrategy;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.apache.tapestry5.services.javascript.ModuleManager;
import org.apache.tapestry5.services.javascript.StylesheetLink;

public class JavaScriptStackAssemblerOverride implements JavaScriptStackAssembler {
	private static final ContentType CSS_CONTENT_TYPE = new ContentType("text/css;charset=utf-8");

	private static final ContentType JAVASCRIPT_CONTENT_TYPE = new ContentType("text/javascript;charset=utf-8");

	private final ThreadLocale threadLocale;

	private final ResourceChangeTracker resourceChangeTracker;

	private final StreamableResourceSource streamableResourceSource;

	private final JavaScriptStackSource stackSource;

	private final AssetChecksumGenerator checksumGenerator;

	private final ModuleManager moduleManager;

	private final ResourceMinimizer resourceMinimizer;

	private final boolean minificationEnabled;

	private final Map<String, StreamableResource> cache = CollectionFactory.newCaseInsensitiveMap();

	private class Parameters {
		final boolean css;
		final Locale locale;
		final String stackName;
		final boolean compress;
		final JavaScriptAggregationStrategy javascriptAggregationStrategy;

		private Parameters(Locale locale, boolean css, String stackName, boolean compress,
				JavaScriptAggregationStrategy javascriptAggregationStrategy) {
			this.css = css;
			this.locale = locale;
			this.stackName = stackName;
			this.compress = compress;
			this.javascriptAggregationStrategy = javascriptAggregationStrategy;
		}

		Parameters disableCompress() {
			return new Parameters(locale, css, stackName, false, javascriptAggregationStrategy);
		}

	}

	// TODO: Support for aggregated CSS as well as aggregated JavaScript

	public JavaScriptStackAssemblerOverride(ThreadLocale threadLocale,
			ResourceChangeTracker resourceChangeTracker,
			StreamableResourceSource streamableResourceSource,
			JavaScriptStackSource stackSource,
			AssetChecksumGenerator checksumGenerator,
			ModuleManager moduleManager,
			ResourceMinimizer resourceMinimizer,
			@Symbol(SymbolConstants.MINIFICATION_ENABLED) boolean minificationEnabled) {
		this.threadLocale = threadLocale;
		this.resourceChangeTracker = resourceChangeTracker;
		this.streamableResourceSource = streamableResourceSource;
		this.stackSource = stackSource;
		this.checksumGenerator = checksumGenerator;
		this.moduleManager = moduleManager;
		this.resourceMinimizer = resourceMinimizer;
		this.minificationEnabled = minificationEnabled;

		resourceChangeTracker.addInvalidationCallback(new Runnable() {
			@Override
			public void run() {
				cache.clear();
			}
		});
	}

	@Override
	public StreamableResource assembleJavaScriptResourceForStack(String stackName, boolean compress,
			JavaScriptAggregationStrategy javascriptAggregationStrategy) throws IOException {
		Locale locale = threadLocale.getLocale();
		boolean css = stackName.startsWith("css#");
		if (css) {
			stackName = stackName.substring(4);
		}

		return assembleJavascriptResourceForStack(new Parameters(locale, css, stackName, compress, javascriptAggregationStrategy));
	}

	private StreamableResource assembleJavascriptResourceForStack(Parameters parameters) throws IOException {
		String key = parameters.stackName + '[' + (parameters.css ? "CSS" : "JAVASCRIPT") + ']'
				+ '[' + (parameters.compress ? "COMPRESS" : "UNCOMPRESSED") + ']' +
				parameters.locale.toString();

		StreamableResource result = cache.get(key);

		if (result == null) {
			result = assemble(parameters);
			cache.put(key, result);
		}

		return result;
	}

	private StreamableResource assemble(Parameters parameters) throws IOException {
		if (parameters.compress) {
			StreamableResource uncompressed = assembleJavascriptResourceForStack(parameters.disableCompress());

			return new CompressedStreamableResource(uncompressed, checksumGenerator);
		}

		JavaScriptStack stack = stackSource.getStack(parameters.stackName);

		return assembleStreamableForStack(parameters.locale.toString(), parameters, stack);
	}

	interface StreamableReader {
		/**
		 * Reads the content of a StreamableResource as a UTF-8 string, and
		 * optionally transforms it in some way.
		 */
		String read(StreamableResource resource) throws IOException;
	}

	static String getContent(StreamableResource resource) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream(resource.getSize());
		resource.streamTo(bos);

		return new String(bos.toByteArray(), "UTF-8");
	}

	final StreamableReader libraryReader = new StreamableReader() {
		@Override
		public String read(StreamableResource resource) throws IOException {
			return getContent(resource);
		}
	};

	private final static Pattern DEFINE = Pattern.compile("\\bdefine\\s*\\((?!\\s*['\"])");

	private static class ModuleReader implements StreamableReader {
		final String moduleName;

		private ModuleReader(String moduleName) {
			this.moduleName = moduleName;
		}

		@Override
		public String read(StreamableResource resource) throws IOException {
			String content = getContent(resource);

			return transform(content);
		}

		public String transform(String moduleContent) {
			return DEFINE.matcher(moduleContent).replaceFirst("define(\"" + moduleName + "\",");
		}
	}

	private class Assembly {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2000);
		final PrintWriter writer;
		long lastModified = 0;
		final StringBuilder description;
		final ContentType type;
		private String sep = "";

		private Assembly(String description, ContentType type) throws UnsupportedEncodingException {
			writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			this.description = new StringBuilder(description);
			this.type = type;
		}

		void add(Resource resource, StreamableReader reader) throws IOException {
			writer.append("\n/* ").append(resource.toString()).append(" */\n");

			description.append(sep).append(resource.toString());
			sep = ", ";

			StreamableResource streamable = streamableResourceSource.getStreamableResource(resource,
					StreamableResourceProcessing.FOR_AGGREGATION, resourceChangeTracker);

			writer.print(reader.read(streamable));

			lastModified = Math.max(lastModified, streamable.getLastModified());
		}

		StreamableResource finish() {
			writer.close();

			return new StreamableResourceImpl(description.toString(), type, CompressionStatus.COMPRESSABLE, lastModified,
					new BytestreamCache(outputStream), checksumGenerator, null);
		}
	}

	private StreamableResource assembleStreamableForStack(String localeName, Parameters parameters,
			JavaScriptStack stack) throws IOException {
		Assembly assembly = parameters.css
				? new Assembly(String.format("'%s' CSS/LESS stack, for locale %s, resources=", parameters.stackName, localeName),
						CSS_CONTENT_TYPE)
				: new Assembly(String.format("'%s' JavaScript stack, for locale %s, resources=", parameters.stackName, localeName),
						JAVASCRIPT_CONTENT_TYPE);

		if (parameters.css) {
			for (StylesheetLink library : stack.getStylesheets()) {
				try {
					Resource resource = ReflectionUtils.getAsset(library).getResource();
					assembly.add(resource, libraryReader);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		} else {

			for (Asset library : stack.getJavaScriptLibraries()) {
				Resource resource = library.getResource();
				assembly.add(resource, libraryReader);
			}

			for (String moduleName : stack.getModules()) {
				Resource resource = moduleManager.findResourceForModule(moduleName);
				if (resource == null) {
					throw new IllegalArgumentException(String.format("Could not identify a resource for module name '%s'.", moduleName));
				}
				assembly.add(resource, new ModuleReader(moduleName));
			}
		}

		StreamableResource streamable = assembly.finish();

		if (minificationEnabled && parameters.javascriptAggregationStrategy.enablesMinimize()) {
			return resourceMinimizer.minimize(streamable);
		}

		return streamable;
	}
}
