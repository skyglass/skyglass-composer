package microservices.book.testutils.http;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathUtil {
	private static Pattern ROOT_PROJECT_PATTERN = Pattern.compile("^(.*?)\\/social-cloud.*?$");

	static String getProjectRoot() {
		final String path = PathUtil.class.getResource(".").getFile();
		final Matcher matcher = ROOT_PROJECT_PATTERN.matcher(path);
		boolean isValid = matcher.matches();
		if (!isValid) {
			throw new IllegalStateException("Not a valid path: " + path);
		}
		return matcher.group(1);
	}

	static String getJarfile(String path) {
		File dir = new File(path);
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(dir + "is not a directory");
		}
		File[] files = dir.listFiles(file -> file.getName().contains("social") && file.getName().endsWith("jar"));
		if (files.length != 1) {
			throw new IllegalStateException("Expected only one jar file in dir $path, but found ${files.size}");
		}
		try {
			return files[0].getCanonicalPath();
		} catch (IOException e) {
			throw new IllegalStateException("Not a valid file path: ", e);
		}
	}

}
