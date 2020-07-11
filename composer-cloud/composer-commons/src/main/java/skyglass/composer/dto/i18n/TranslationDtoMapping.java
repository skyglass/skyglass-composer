package skyglass.composer.dto.i18n;

public class TranslationDtoMapping {

	private String fieldName;

	private String fallbackFieldName;

	private TranslationDtoMapping(String fieldName) {
		this.fieldName = fieldName;
	}

	public static TranslationDtoMapping create(String fieldName) {
		return new TranslationDtoMapping(fieldName);
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

	public TranslationDtoMapping fallbackFieldName(String fallbackFieldName) {
		this.setFallbackFieldName(fallbackFieldName);

		return this;
	}
}
