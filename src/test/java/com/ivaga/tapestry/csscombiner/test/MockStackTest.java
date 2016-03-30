package com.ivaga.tapestry.csscombiner.test;

import org.apache.tapestry5.SymbolConstants;
import org.junit.*;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockStackTest extends Assert {

	private static final int port = 7788;
	private static Server server;

	private StringBuffer response = new StringBuffer();

	@BeforeClass
	public static void startServer() throws Exception {
		System.setProperty(SymbolConstants.PRODUCTION_MODE, "false");
		System.setProperty(SymbolConstants.EXECUTION_MODE, "development");
		server = new Server(port);
		server.setStopAtShutdown(true);
		WebAppContext context = new WebAppContext("src/test/webapp", "/");
		server.setHandler(context);
		server.start();
	}

	@Before
	public void setResponse() {
		response = new StringBuffer();
	}

	@Test
    public void checkLess() throws Exception {
        int code = get("http://localhost:" + port + "/stack3", response);
        assertEquals(200, code);
        assertTrue(response.toString().contains("/less1.less"));

        String urlCss = find("href=\"(/assets/[^\"']+/less1\\.less)");
        assertNotNull(urlCss);
        response = new StringBuffer();
        code = get("http://localhost:" + port + urlCss, response);
        assertEquals(200, code);
        assertTrue(response.toString().replaceAll(" ", "").contains("head{font-size:1px"));
        assertTrue(response.toString().replaceAll(" ", "").contains("body{font-size:1px"));
    }

    @Test
	public void checkStackCombined() throws Exception {
		int code = get("http://localhost:" + port + "/stack1", response);
		assertEquals(200, code);
		assertTrue(response.toString().contains("/stack1.css"));
		assertTrue(response.toString().contains("/stack1.js"));
		assertFalse(response.toString().contains("/js1.js"));
		assertFalse(response.toString().contains("/js2.js"));

		assertFalse(response.toString().contains("/stack2.js"));

		String urlCss = find("href=\"(/assets/[^\"']+/stack1\\.css)");
		String urlJs = find("\"(/assets/[^\"']+/stack1\\.js)");
		assertNotNull(urlCss);
		assertNotNull(urlJs);

		response = new StringBuffer();
		code = get("http://localhost:" + port + urlCss, response);
		assertEquals(200, code);
		assertTrue(response.toString().contains("head{}"));
		assertTrue(response.toString().contains("body{}"));

		response = new StringBuffer();
		code = get("http://localhost:" + port + urlJs, response);
		assertEquals(200, code);
		assertTrue(response.toString().contains("var b={c:\"123\"};"));
		assertTrue(response.toString().contains("var i={a:\"123\"};"));
	}

	public String find(String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(response.toString());
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	@Test
	public void checkStackUncombined() throws Exception {
		int code = get("http://localhost:" + port + "/stack2", response);
		assertEquals(200, code);
		assertFalse(response.toString().contains("/stack2.js"));

		assertTrue(response.toString().contains("/style1.css"));
		assertTrue(response.toString().contains("/style2.css"));
		assertTrue(response.toString().contains("/js1.js"));
		assertTrue(response.toString().contains("/js2.js"));
	}

	@AfterClass
	public static void shutdownServer() throws Exception {
		try {
			server.stop();
		} catch (Exception e) {

		}
	}

	private static int get(String url, StringBuffer responseBuff) throws MalformedURLException, IOException {
		URLConnection connection = new URL(url).openConnection();
		int code = 0;
		try {
			((HttpURLConnection) connection).setRequestMethod("GET");
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			// connection.setRequestProperty("accept",
			// "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			connection.setRequestProperty("cache-control", "no-cache");
			connection.setRequestProperty("pragma", "no-cache");
			code = ((HttpURLConnection) connection).getResponseCode();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				responseBuff.append(line);
			}
			reader.close();
			System.out.println(code);
			System.out.println(responseBuff);
		} finally {
			((HttpURLConnection) connection).disconnect();
		}
		return code;
	}

	public static void main(String[] args) {
		Pattern p = Pattern.compile("(/assets/[^\"']+/stack1\\.js)");
		Matcher m = p.matcher(
				"function(pi) { pi([  \"/assets/stack/92af21e7/ru/stack1.js\"], []); });");
		m.find();
		System.out.println(m.group(1));
	}
}