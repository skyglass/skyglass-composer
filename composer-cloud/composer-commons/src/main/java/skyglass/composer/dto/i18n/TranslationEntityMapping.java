package skyglass.composer.dto.i18n;

public class TranslationEntityMapping {

	private String fieldName;

	private String fallbackFieldName;

	private TranslationEntityMapping(String fieldName) {
		this.fieldName = fieldName;
	}

	public static TranslationEntityMapping create(String fieldName) {
		return new TranslationEntityMapping(fieldName);
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFallbackFieldName() {
		return fallbackFieldName;
	}

	public void setFallbackFieldName(String fallbackFieldName) {
		this.fallbackFieldName = fallbackFieldName;
	}

	public TranslationEntityMapping fallbackFieldName(String fallbackFieldName) {
		this.setFallbackFieldName(fallbackFieldName);

		return this;
	}
}
