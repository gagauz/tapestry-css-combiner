package com.ivaga.tapestry.csscombiner.test.services;

import com.ivaga.tapestry.csscombiner.CssCombinerModule;
import com.ivaga.tapestry.csscombiner.LessModule;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.javascript.*;

import java.util.List;

@ImportModule({ CssCombinerModule.class, LessModule.class })
public class AppModule {

	@Contribute(JavaScriptStackSource.class)
	public static void contributeStack1(MappedConfiguration<String, JavaScriptStack> configuration,
			AssetSource assetSource) {

		List<StackExtension> extensions = CollectionFactory.newList();
		extensions.add(StackExtension.stylesheet("/META-INF/assets/style1.css"));
		extensions.add(StackExtension.stylesheet("/META-INF/assets/style2.css"));
		extensions.add(StackExtension.library("/META-INF/assets/js1.js"));
		extensions.add(StackExtension.library("/META-INF/assets/js2.js"));
		extensions.add(StackExtension.javascriptAggregation(JavaScriptAggregationStrategy.COMBINE_AND_MINIMIZE));

		configuration.add("stack1", new ExtensibleJavaScriptStack(assetSource, extensions));
	}

	@Contribute(JavaScriptStackSource.class)
	public static void contributeStack2(MappedConfiguration<String, JavaScriptStack> configuration,
			AssetSource assetSource) {

		List<StackExtension> extensions = CollectionFactory.newList();
		extensions.add(StackExtension.stylesheet("/META-INF/assets/style1.css"));
		extensions.add(StackExtension.stylesheet("/META-INF/assets/style2.css"));
		extensions.add(StackExtension.library("/META-INF/assets/js1.js"));
		extensions.add(StackExtension.library("/META-INF/assets/js2.js"));
		extensions.add(StackExtension.javascriptAggregation(JavaScriptAggregationStrategy.DO_NOTHING));

		configuration.add("stack2", new ExtensibleJavaScriptStack(assetSource, extensions));
	}

    @Contribute(JavaScriptStackSource.class)
    public static void contributeStack3(MappedConfiguration<String, JavaScriptStack> configuration,
            AssetSource assetSource) {

        List<StackExtension> extensions = CollectionFactory.newList();
        extensions.add(StackExtension.stylesheet("/META-INF/assets/less1.less"));
        extensions.add(StackExtension.javascriptAggregation(JavaScriptAggregationStrategy.DO_NOTHING));
        configuration.add("stack3", new ExtensibleJavaScriptStack(assetSource, extensions));
    }

	@ApplicationDefaults
	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.MINIFICATION_ENABLED, true);
		configuration.add(SymbolConstants.ENABLE_HTML5_SUPPORT, true);
		configuration.add(SymbolConstants.COMBINE_SCRIPTS, true);
		configuration.add(SymbolConstants.JAVASCRIPT_INFRASTRUCTURE_PROVIDER, "jquery");
		configuration.add(SymbolConstants.ENCODE_LOCALE_INTO_PATH, false);
		configuration.add(SymbolConstants.CHARSET, "utf-8");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE, "aaf8f^&*%ewrfw");
	}
}
