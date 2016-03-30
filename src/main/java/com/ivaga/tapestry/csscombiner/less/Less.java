package com.ivaga.tapestry.csscombiner.less;

import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

/**
 * The main class of JLessC library. Its contain all start points for converting
 * LESS to CSS files.
 */
public class Less {

	public static String compile(URL baseURL, String lessData, boolean compress) throws LessException {
		return compile(baseURL, new StringReader(lessData), compress, new ReaderFactory());
	}

	public static String compile(URL baseURL, Reader lessData, boolean compress) throws LessException {
		return compile(baseURL, lessData, compress, new ReaderFactory());
	}

	/**
	 * Compile the less data from a string.
	 *
	 * @param baseURL
	 *            the baseURL for import of external less data.
	 * @param lessData
	 *            the input less data
	 * @param compress
	 *            true, if the CSS data should be compressed without any extra
	 *            formating characters.
	 * @param readerFactory
	 *            A factory for the readers for imports.
	 * @return the resulting less data
	 * @throws LessException
	 *             if any error occur on compiling.
	 */

	public static String compile(URL baseURL, Reader lessData, boolean compress, ReaderFactory readerFactory) throws LessException {
		try {
			LessParser parser = new LessParser();
			parser.parse(baseURL, lessData, readerFactory);

			StringBuilder builder = new StringBuilder();
			CssFormatter formatter = compress ? new CompressCssFormatter() : new CssFormatter();
			parser.parseLazy(formatter);
			formatter.format(parser, baseURL, builder);
			return builder.toString();
		} catch (LessException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new LessException(ex);
		}
	}

}
