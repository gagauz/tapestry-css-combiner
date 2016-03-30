package com.ivaga.tapestry.csscombiner;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.assets.ResourceChangeTracker;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.apache.tapestry5.services.assets.StreamableResourceSource;

public class LessModule {

	@Contribute(StreamableResourceSource.class)
	public static void provideCompilers(MappedConfiguration<String, ResourceTransformer> configuration,
			@Autobuild ResourceTransformerFactory factory,
            final ResourceChangeTracker tracker) {
		configuration.add("less", factory.wrapCompiler("text/css", "Less", "CSS", new LessResourceTransformer()));
	}
}
