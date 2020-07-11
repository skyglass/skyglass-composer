package skyglass.composer.domain.orgunit;

import javax.persistence.ManyToOne;

import skyglass.composer.domain.businesspartner.BusinessPartner;
import skyglass.composer.entity.AEntity;

public class OrgUnit_OwnerView extends AEntity {

	private static final long serialVersionUID = -9040769467910646734L;

	@ManyToOne
	private BusinessPartner owner;

}
