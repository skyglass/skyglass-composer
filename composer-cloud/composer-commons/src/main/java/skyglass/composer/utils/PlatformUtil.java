package skyglass.composer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyglass.composer.security.dto.ExtUserDTO;
import skyglass.composer.security.dto.ExtUserNameDTO;

public class PlatformUtil {
	private static final Logger log = LoggerFactory.getLogger(PlatformUtil.class);

	public static final String USERNAME_PROP_NAME = "username";

	private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

	private static final String LIBRARY_NAME = "commons";

	public static boolean isLocalDevelopment(HttpServletRequest request) {
		return isLocalDevelopment(request.getRequestURL().toString());
	}

	public static boolean isLocalDevelopment(String url) {
		String patternString = ".*http://localhost.*";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(url);
		return matcher.matches();
	}

	public static ExtUserDTO createDummyUser(String name) {
		ExtUserDTO dto = new ExtUserDTO();
		ExtUserNameDTO nameDTO = new ExtUserNameDTO(name);
		nameDTO.setGivenName(name);
		dto.setName(nameDTO);
		dto.setId(name);
		return dto;
	}

	public static String getVersion() {
		return getManifestValue("Implementation-Version");
	}

	public static String getBuildNumber() {
		return getManifestValue("Implementation-Build");
	}

	private static String getManifestValue(String manifestAttributeName) {
		try {
			Enumeration<URL> resources = PlatformUtil.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();

				Manifest mf = new Manifest();

				try (InputStream is = resource.openStream()) {
					mf.read(is);

					Attributes attribs = mf.getMainAttributes();
					String title = attribs.getValue("Implementation-Title");
					if (LIBRARY_NAME.equals(title)) {
						String version = attribs.getValue(manifestAttributeName);
						if (StringUtils.isBlank(version)) {
							version = "N/A";
						}

						return version;
					}
				} catch (IOException ex) {
					// ignore
				}
			}
		} catch (IOException ex) {
			return "N/A";
		}

		return "N/A";
	}

	public static void setUsernameInThreadLocal(String username) {
		threadLocal.set(username);
	}

	public static String getUsernameFromThreadLocal() {
		return threadLocal.get();
	}

	public static String getUsernameFromCtx() {
		String username = null;

		try {
			username = PlatformUtil.getUsernameFromThreadLocal();
		} catch (Throwable ex) {
			log.error("Could not get user info", ex);
		}

		return username != null ? username.toUpperCase() : null;
	}

}
