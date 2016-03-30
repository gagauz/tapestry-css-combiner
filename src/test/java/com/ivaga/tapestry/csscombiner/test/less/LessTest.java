package com.ivaga.tapestry.csscombiner.test.less;

import com.ivaga.tapestry.csscombiner.less.Less;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class LessTest {

	private File lessFile;
	private File cssFile;

	public LessTest(String name, File lessFile, File cssFile) {
		this.lessFile = lessFile;
		this.cssFile = cssFile;
	}

	@Parameters(name = "{0}")
	public static List<Object[]> params() throws Exception {
		ArrayList<Object[]> params = new ArrayList<>();
		URL url = LessTest.class.getResource("/samples");
		File dir = new File(url.toURI());
		int baseLength = dir.getPath().length() + 1;
		params(params, dir, baseLength);
		return params;
	}

	private static void params(ArrayList<Object[]> params, File dir, int baseLength) {
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".less")) {
				String basename = file.getName();
				basename = basename.substring(0, basename.length() - 5);
				String cssname = basename + ".css";
				File cssfile = new File(file.getParent(), cssname);
				if (cssfile.exists()) {
					params.add(new Object[] { cssfile.getPath().substring(baseLength), file, cssfile });
				}
				cssname = basename + ".css_x";
				cssfile = new File(file.getParent(), cssname);
				if (cssfile.exists()) {
					params.add(new Object[] { cssfile.getPath().substring(baseLength), file, cssfile });
				}
			} else if (file.isDirectory()) {
				params(params, file, baseLength);
			}
		}
	}

	@Test
	public void compile() throws Exception {
		String cssData = new String(Files.readAllBytes(cssFile.toPath()), StandardCharsets.UTF_8);
		boolean compress = cssFile.getName().endsWith(".css_x") || lessFile.getParentFile().getName().equals("compression");
		String lessData = Less.compile(lessFile.toURI().toURL(), new FileReader(lessFile), compress);
		lessData = lessData.replaceAll("\r", "");
		cssData = cssData.replaceAll("\r", "");

		System.out.println("Comparing " + lessFile.getName() + " with " + cssFile.getName());
		String[] exp = cssData.split("\n");
		String[] act = lessData.split("\n");

		for (int i = 0; i < exp.length; i++) {
			assertEquals("Line[" + i + "]", exp[i].trim(), act[i].trim());
		}
	}

	public static String readFully(InputStream inputStream, String encoding)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, length);
		}
		return baos.toString();
	}
}
