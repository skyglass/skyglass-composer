package skyglass.composer.domain.businesspartner;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import skyglass.composer.entity.AEntity;

@Entity
public class Supplier extends AEntity {

	private static final long serialVersionUID = -995697602788620564L;

	@OneToOne(cascade = { CascadeType.PERSIST }, optional = false, fetch = FetchType.LAZY)
	private BusinessPartner businessPartner;

	public BusinessPartner getBusinessPartner() {
		return businessPartner;
	}

	public void setBusinessPartner(BusinessPartner businessPartner) {
		this.businessPartner = businessPartner;
	}

}
