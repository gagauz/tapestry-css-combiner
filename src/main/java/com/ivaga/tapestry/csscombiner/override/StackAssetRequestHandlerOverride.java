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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry5.internal.services.ResourceStreamer;
import org.apache.tapestry5.internal.services.assets.JavaScriptStackAssembler;
import org.apache.tapestry5.ioc.IOOperation;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.services.LocalizationSetter;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.assets.AssetRequestHandler;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.slf4j.Logger;

public class StackAssetRequestHandlerOverride implements AssetRequestHandler {
	private final Logger logger;

	private final LocalizationSetter localizationSetter;

	private final ResourceStreamer resourceStreamer;

	// Group 1: checksum
	// Group 2: locale
	// Group 3: path
	private final Pattern pathPattern = Pattern.compile("^(.+)/(.+)/(.+)\\.(js|css)$");

	private final OperationTracker tracker;

	private final JavaScriptStackAssembler javaScriptStackAssembler;

	private final JavaScriptStackSource stackSource;

	public StackAssetRequestHandlerOverride(Logger logger, LocalizationSetter localizationSetter,
			ResourceStreamer resourceStreamer,
			OperationTracker tracker,
			JavaScriptStackAssembler javaScriptStackAssembler,
			JavaScriptStackSource stackSource) {
		this.logger = logger;
		this.localizationSetter = localizationSetter;
		this.resourceStreamer = resourceStreamer;
		this.tracker = tracker;
		this.javaScriptStackAssembler = javaScriptStackAssembler;
		this.stackSource = stackSource;
	}

	@Override
	public boolean handleAssetRequest(Request request, Response response, final String extraPath) throws IOException {
		String log = "Streaming asset stack " + extraPath;
		return tracker.perform(log,
				new IOOperation<Boolean>() {
					@Override
					public Boolean perform() throws IOException {
						return streamStackResource(extraPath);
					}
				});
	}

	private boolean streamStackResource(String extraPath) throws IOException {
		Matcher matcher = pathPattern.matcher(extraPath);

		if (!matcher.matches()) {
			logger.warn("Unable to parse '" + extraPath + "' as an asset stack path");

			return false;
		}

		String checksum = matcher.group(1);
		String localeName = matcher.group(2);
		final String stackName = matcher.group(3);
		final String stackType = matcher.group(4);

		final boolean compressed = checksum.startsWith("z");

		if (compressed) {
			checksum = checksum.substring(1);
		}

		final JavaScriptStack stack = stackSource.findStack(stackName);

		if (stack == null) {
			logger.warn(String.format("JavaScript stack '%s' not found.", stackName));
			return false;
		}

		// Yes, I have a big regret that the JavaScript stack stuff relies on
		// this global, rather than
		// having it passed around properly.

		localizationSetter.setNonPersistentLocaleFromLocaleName(localeName);

		StreamableResource resource = tracker.perform(String.format("Assembling JavaScript asset stack '%s' (%s)", stackName, localeName),
				new IOOperation<StreamableResource>() {
					@Override
					public StreamableResource perform() throws IOException {
						String stName = stackType.equals("css") ? "css#" + stackName : stackName;
						return javaScriptStackAssembler.assembleJavaScriptResourceForStack(stName, compressed,
								stack.getJavaScriptAggregationStrategy());
					}
				});

		if (resource == null) {
			return false;
		}

		return resourceStreamer.streamResource(resource, checksum, ResourceStreamer.DEFAULT_OPTIONS);
	}
}
