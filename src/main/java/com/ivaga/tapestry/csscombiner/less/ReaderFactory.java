package com.ivaga.tapestry.csscombiner.less;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * A factory to create a reader for parsing. You can override it to resolve the
 * URL, implement a cache or use another encoding as UFT-8.
 */
public class ReaderFactory {

	/**
	 * Create a Reader for the given URL.
	 *
	 * @param url
	 *            the url, not null
	 * @return the reader, never null
	 * @throws IOException
	 *             If any I/O error occur on reading the URL.
	 */
	public Reader create(URL url) throws IOException {
		return new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
	}
}
