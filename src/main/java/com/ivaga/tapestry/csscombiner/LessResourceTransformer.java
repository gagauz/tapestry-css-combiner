package com.ivaga.tapestry.csscombiner;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.ivaga.tapestry.csscombiner.less.Less;
import com.ivaga.tapestry.csscombiner.less.LessException;
import com.ivaga.tapestry.csscombiner.less.ReaderFactory;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.internal.services.UrlResource;
import org.apache.tapestry5.internal.services.assets.BytestreamCache;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LessResourceTransformer implements ResourceTransformer {
	private static final Logger log = LoggerFactory.getLogger(LessResourceTransformer.class);
	private static final ContentType CSS = new ContentType("text/css");

	public LessResourceTransformer() {
	}

	@Override
	public ContentType getTransformedContentType() {
		return CSS;
	}

	@Override
	public InputStream transform(Resource source, ResourceDependencies dependencies) throws IOException {
		BytestreamCache compiled = invokeLessCompiler(source, dependencies);
		return compiled.openStream();
	}

	private BytestreamCache invokeLessCompiler(Resource source, final ResourceDependencies dependencies) throws IOException {
		try {
			StringReader reader;
			InputStream is = source.openStream();
			Scanner s = null;
			try {
				s = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
				reader = new StringReader(s.hasNext() ? s.next() : "");
			} finally {
				s.close();
				is.close();
			}

			String css = Less.compile(source.toURL(), reader, false,
					new ReaderFactory() {
						@Override
						public Reader create(URL url) throws IOException {
							log.info("Track dependency " + url);
							dependencies.addDependency(new UrlResource(url));
							return super.create(url);
						}
					});
			return new BytestreamCache(css.getBytes("utf-8"));

		} catch (LessException ex) {
			throw new IOException(ex);
		}
	}

}