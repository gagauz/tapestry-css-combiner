// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.ivaga.tapestry.csscombiner.override;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Mapper;
import org.apache.tapestry5.internal.services.RequestConstants;
import org.apache.tapestry5.internal.services.assets.JavaScriptStackAssembler;
import org.apache.tapestry5.internal.services.javascript.JavaScriptStackPathConstructor;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.ioc.util.ExceptionUtils;
import org.apache.tapestry5.services.ResponseCompressionAnalyzer;
import org.apache.tapestry5.services.assets.AssetPathConstructor;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.apache.tapestry5.services.javascript.StylesheetLink;

import java.io.IOException;
import java.util.List;

public class JavaScriptStackPathConstructorOverride implements JavaScriptStackPathConstructor {
	private final ThreadLocale threadLocale;
	private final AssetPathConstructor assetPathConstructor;
	private final JavaScriptStackSource javascriptStackSource;
	private final JavaScriptStackAssembler assembler;
	private final ResponseCompressionAnalyzer compressionAnalyzer;
	private final boolean combineScripts;

	private final Mapper<Asset, String> toPath = new Mapper<Asset, String>() {
		@Override
		public String map(Asset input) {
			return input.toClientURL();
		}
	};

	private final Mapper<StylesheetLink, Asset> toAsset = new Mapper<StylesheetLink, Asset>() {
		@Override
		public Asset map(StylesheetLink input) {
			return ReflectionUtils.getAsset(input);
		}
	};

	public JavaScriptStackPathConstructorOverride(JavaScriptStackPathConstructor original,
			ThreadLocale threadLocale,
			AssetPathConstructor assetPathConstructor,
			JavaScriptStackSource javascriptStackSource,
			JavaScriptStackAssembler assembler,
			ResponseCompressionAnalyzer compressionAnalyzer,
			@Symbol(SymbolConstants.COMBINE_SCRIPTS) boolean combine) {
		this.threadLocale = threadLocale;
		this.assetPathConstructor = assetPathConstructor;
		this.javascriptStackSource = javascriptStackSource;
		this.assembler = assembler;
		this.compressionAnalyzer = compressionAnalyzer;
		this.combineScripts = combine;
	}

    @Override
	public List<String> constructPathsForJavaScriptStack(String stackName) {
		JavaScriptStack stack = javascriptStackSource.getStack(stackName);

		List<Asset> assets = stack.getJavaScriptLibraries();
		List<StylesheetLink> styles = stack.getStylesheets();
		List<String> result = CollectionFactory.newList();

		final boolean ebabledCombine = combineScripts && stack.getJavaScriptAggregationStrategy().enablesCombine();

		if (ebabledCombine && (assets.size() > 1 || !stack.getModules().isEmpty())) {
            result.add(combinedStackURL(stackName, stack));
		} else {
		    result.addAll(toPaths(assets));
		}

		if (ebabledCombine && styles.size() > 1) {
		    result.add(combinedStackURLCss(stackName, stack));
        } else {
            result.addAll(toPaths(F.flow(styles).map(toAsset).toList()));
        }

        return result;
	}

	private List<String> toPaths(List<Asset> assets) {
		assert assets != null;

		return F.flow(assets).map(toPath).toList();
	}

	private String combinedStackURLCss(String stackName, JavaScriptStack stack) {
		try {

			StreamableResource assembled = assembler.assembleJavaScriptResourceForStack("css#" + stackName,
					compressionAnalyzer.isGZipSupported(),
					stack.getJavaScriptAggregationStrategy());

			String path = threadLocale.getLocale().toString() + '/' + stackName + ".css";

			return assetPathConstructor.constructAssetPath(RequestConstants.STACK_FOLDER, path, assembled);
		} catch (IOException ex) {
			throw new RuntimeException(String.format("Unable to construct path for '%s' JavaScript stack: %s",
					stackName,
					ExceptionUtils.toMessage(ex)), ex);
		}
	}

	private String combinedStackURL(String stackName, JavaScriptStack stack) {
		try {

			StreamableResource assembled = assembler.assembleJavaScriptResourceForStack(stackName, compressionAnalyzer.isGZipSupported(),
					stack.getJavaScriptAggregationStrategy());

			String path = threadLocale.getLocale().toString() + '/' + stackName + ".js";

			return assetPathConstructor.constructAssetPath(RequestConstants.STACK_FOLDER, path, assembled);
		} catch (IOException ex) {
			throw new RuntimeException(String.format("Unable to construct path for '%s' JavaScript stack: %s",
					stackName,
					ExceptionUtils.toMessage(ex)), ex);
		}
	}

}
