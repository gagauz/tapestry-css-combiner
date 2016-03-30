package com.ivaga.tapestry.csscombiner;

import com.ivaga.tapestry.csscombiner.override.JavaScriptStackAssemblerOverride;
import com.ivaga.tapestry.csscombiner.override.JavaScriptStackPathConstructorOverride;
import com.ivaga.tapestry.csscombiner.override.JavaScriptSupportOverride;
import com.ivaga.tapestry.csscombiner.override.StackAssetRequestHandlerOverride;
import org.apache.tapestry5.BooleanHook;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.services.DocumentLinker;
import org.apache.tapestry5.internal.services.RequestConstants;
import org.apache.tapestry5.internal.services.ResourceStreamer;
import org.apache.tapestry5.internal.services.assets.JavaScriptStackAssembler;
import org.apache.tapestry5.internal.services.assets.ResourceChangeTracker;
import org.apache.tapestry5.internal.services.javascript.JavaScriptStackPathConstructor;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Decorate;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.ioc.util.IdAllocator;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.assets.*;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.javascript.ModuleManager;
import org.slf4j.Logger;

public class CssCombinerModule {


	@Contribute(Dispatcher.class)
	@AssetRequestDispatcher
	public static void provideBuiltinAssetDispatchers(MappedConfiguration<String, AssetRequestHandler> configuration,
			Logger logger,
			LocalizationSetter localizationSetter,
			ResourceStreamer resourceStreamer,
			OperationTracker tracker,
			JavaScriptStackAssembler javaScriptStackAssembler,
			JavaScriptStackSource stackSource) {
		configuration.override(RequestConstants.STACK_FOLDER, new StackAssetRequestHandlerOverride(logger, localizationSetter, resourceStreamer,
				tracker, javaScriptStackAssembler, stackSource));
	}

	@Decorate(serviceInterface = JavaScriptStackPathConstructor.class)
	public static JavaScriptStackPathConstructor decorate(JavaScriptStackPathConstructor original,
			ThreadLocale threadLocale,
			AssetPathConstructor assetPathConstructor,
			JavaScriptStackSource javascriptStackSource,
			JavaScriptStackAssembler assembler,
			ResponseCompressionAnalyzer compressionAnalyzer,
			@Symbol(SymbolConstants.COMBINE_SCRIPTS) boolean combine) {
		return new JavaScriptStackPathConstructorOverride(original, threadLocale, assetPathConstructor, javascriptStackSource, assembler,
				compressionAnalyzer,
				combine);
	}

	@Decorate(serviceInterface = JavaScriptStackAssembler.class)
	public static JavaScriptStackAssembler decorate(ThreadLocale threadLocale,
			ResourceChangeTracker resourceChangeTracker,
			StreamableResourceSource streamableResourceSource,
			JavaScriptStackSource stackSource,
			AssetChecksumGenerator checksumGenerator,
			ModuleManager moduleManager,
			ResourceMinimizer resourceMinimizer,
			@Symbol(SymbolConstants.MINIFICATION_ENABLED) boolean minificationEnabled) {
		return new JavaScriptStackAssemblerOverride(threadLocale, resourceChangeTracker, streamableResourceSource, stackSource,
				checksumGenerator,
				moduleManager, resourceMinimizer, minificationEnabled);
	}

	@Contribute(MarkupRenderer.class)
	public void exposeJavaScriptSupportForFullPageRenders(OrderedConfiguration<MarkupRendererFilter> configuration,
			final Environment environment,
			final JavaScriptStackSource javascriptStackSource,
			final JavaScriptStackPathConstructor javascriptStackPathConstructor,
			final Request request) {

		final BooleanHook suppressCoreStylesheetsHook = createSuppressCoreStylesheetHook(request);

		MarkupRendererFilter javaScriptSupport = new MarkupRendererFilter() {
			@Override
			public void renderMarkup(MarkupWriter writer, MarkupRenderer renderer) {
				DocumentLinker linker = environment.peekRequired(DocumentLinker.class);

				JavaScriptSupportOverride support = new JavaScriptSupportOverride(linker, javascriptStackSource, javascriptStackPathConstructor,
						suppressCoreStylesheetsHook);

				environment.push(JavaScriptSupport.class, support);

				renderer.renderMarkup(writer);

				environment.pop(JavaScriptSupport.class);

				support.commit();
			}
		};

		configuration.override("JavaScriptSupport", javaScriptSupport, "after:DocumentLinker");
	}

	/**
	 * Contributes {@link PartialMarkupRendererFilter}s used when rendering a
	 * partial Ajax response.
	 * <dl>
	 * <dt>JavaScriptSupport
	 * <dd>Provides {@link JavaScriptSupport}</dd>
	 * </dl>
	 */
	@Contribute(PartialMarkupRenderer.class)
	public void exposeJavaScriptSupportForPartialPageRender(OrderedConfiguration<PartialMarkupRendererFilter> configuration,
			final Environment environment,
			final JavaScriptStackSource javascriptStackSource,
			final JavaScriptStackPathConstructor javascriptStackPathConstructor,
			final Request request) {
		final BooleanHook suppressCoreStylesheetsHook = createSuppressCoreStylesheetHook(request);

		PartialMarkupRendererFilter javascriptSupport = new PartialMarkupRendererFilter() {
			@Override
			public void renderMarkup(MarkupWriter writer, JSONObject reply, PartialMarkupRenderer renderer) {
				IdAllocator idAllocator;

				if (request.getParameter(InternalConstants.SUPPRESS_NAMESPACED_IDS) == null) {
					String uid = Long.toHexString(System.nanoTime());

					String namespace = "_" + uid;

					idAllocator = new IdAllocator(namespace);
				} else {
					// When suppressed, work just like normal rendering.
					idAllocator = new IdAllocator();
				}

				DocumentLinker linker = environment.peekRequired(DocumentLinker.class);

				JavaScriptSupportOverride support = new JavaScriptSupportOverride(linker, javascriptStackSource,
						javascriptStackPathConstructor, idAllocator, true, suppressCoreStylesheetsHook);

				environment.push(JavaScriptSupport.class, support);

				renderer.renderMarkup(writer, reply);

				environment.pop(JavaScriptSupport.class);

				support.commit();
			}
		};

		configuration.override("JavaScriptSupport", javascriptSupport, "after:DocumentLinker");
	}

	private BooleanHook createSuppressCoreStylesheetHook(final Request request) {
		return new BooleanHook() {
			@Override
			public boolean checkHook() {
				return request.getAttribute(InternalConstants.SUPPRESS_CORE_STYLESHEETS) != null;
			}
		};
	}

}
