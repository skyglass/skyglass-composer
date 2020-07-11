package skyglass.composer.entity.i18n;

import javax.persistence.Column;
import javax.persistence.Entity;

import skyglass.composer.entity.AEntity;

@Entity
public class TranslatedField extends AEntity {

	private static final long serialVersionUID = 1L;

	@Column
	private String en;

	@Column
	private String referenceUuid;

	public String getEn() {
		return en;
	}

	public void setEn(String en) {
		this.en = en;
	}

	public String getReferenceUuid() {
		return referenceUuid;
	}

	public void setReferenceUuid(String referenceUuid) {
		this.referenceUuid = referenceUuid;
	}
}
