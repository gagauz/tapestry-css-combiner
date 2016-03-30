package com.ivaga.tapestry.csscombiner;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.ioc.IOOperation;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.slf4j.Logger;

//TODO
public class ResourceTransformerFactory {
	private final Logger logger;
	private final OperationTracker tracker;

	public ResourceTransformerFactory(Logger logger,
			OperationTracker tracker) {
		this.logger = logger;
		this.tracker = tracker;
	}

	public ResourceTransformer wrapCompiler(String contentType, String sourceName, String targetName, ResourceTransformer transformer) {
		ResourceTransformer trackingCompiler = wrapWithTracking(sourceName, targetName, transformer);
		return wrapWithTiming(targetName, trackingCompiler);
	}

	private ResourceTransformer wrapWithTracking(final String sourceName, final String targetName, ResourceTransformer core) {
		return new DelegatingResourceTransformer(core) {
			@Override
			public InputStream transform(final Resource source, final ResourceDependencies dependencies) throws IOException {
				final String description = String.format("Compiling %s from %s to %s", source, sourceName, targetName);

				return tracker.perform(description, new IOOperation<InputStream>() {
					@Override
					public InputStream perform() throws IOException {
						return delegate.transform(source, dependencies);
					}
				});
			}
		};
	}

	private ResourceTransformer wrapWithTiming(final String targetName, ResourceTransformer coreCompiler) {
		return new DelegatingResourceTransformer(coreCompiler) {
			@Override
			public InputStream transform(final Resource source, final ResourceDependencies dependencies) throws IOException {
				final long startTime = System.nanoTime();

				InputStream result = delegate.transform(source, dependencies);

				final long elapsedTime = System.nanoTime() - startTime;

				logger.info(String.format("Compiled %s to %s in %.2f ms",
						source, targetName, elapsedTime / 1000000.0d));

				return result;
			}
		};
	}

	public static abstract class DelegatingResourceTransformer implements ResourceTransformer {
		protected final ResourceTransformer delegate;

		protected DelegatingResourceTransformer(ResourceTransformer delegate) {
			this.delegate = delegate;
		}

		@Override
		public ContentType getTransformedContentType() {
			return delegate.getTransformedContentType();
		}
	}

}