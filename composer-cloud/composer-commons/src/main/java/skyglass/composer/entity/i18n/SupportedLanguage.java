package skyglass.composer.entity.i18n;

import org.apache.commons.lang3.StringUtils;

public enum SupportedLanguage {
	EN("en");

	public static final SupportedLanguage DEFAULT = SupportedLanguage.EN;

	private final String languageCode;

	private SupportedLanguage(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public static SupportedLanguage getByLanguageCode(String languageCode) {
		if (StringUtils.isNotBlank(languageCode)) {
			for (SupportedLanguage lang : SupportedLanguage.values()) {
				if (StringUtils.equalsIgnoreCase(languageCode, lang.getLanguageCode())) {
					return lang;
				}
			}
		}
		return SupportedLanguage.DEFAULT;
	}

	public static SupportedLanguage[] getLanguages(String excludedLanguageCode) {
		if (StringUtils.isBlank(excludedLanguageCode)) {
			return values();
		}
		SupportedLanguage[] result = new SupportedLanguage[SupportedLanguage.values().length - 1];
		SupportedLanguage excludedLanguage = SupportedLanguage.getByLanguageCode(excludedLanguageCode);
		int i = 0;
		for (SupportedLanguage lang : SupportedLanguage.values()) {
			if (lang != excludedLanguage) {
				result[i] = lang;
				i++;
			}
		}
		return result;
	}
}
