package skyglass.composer.domain.businesspartner;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import skyglass.composer.entity.AEntity;

@Entity
public class BusinessCustomer extends AEntity {

	private static final long serialVersionUID = 6478377354474492454L;

	@OneToOne(cascade = { CascadeType.PERSIST }, optional = false)
	private BusinessPartner businessPartner;

	public BusinessPartner getBusinessPartner() {
		return businessPartner;
	}

	public void setBusinessPartner(BusinessPartner businessPartner) {
		this.businessPartner = businessPartner;
	}

}
