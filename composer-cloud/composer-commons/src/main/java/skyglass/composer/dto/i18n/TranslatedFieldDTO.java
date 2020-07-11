package skyglass.composer.dto.i18n;

import skyglass.composer.dto.AEntityDTO;

public class TranslatedFieldDTO extends AEntityDTO {

	private static final long serialVersionUID = 1L;

	private String en;

	public String getEn() {
		return en;
	}

	public void setEn(String en) {
		this.en = en;
	}

	private String referenceUuid;

	public String getReferenceUuid() {
		return referenceUuid;
	}

	public void setReferenceUuid(String referenceUuid) {
		this.referenceUuid = referenceUuid;
	}
}
