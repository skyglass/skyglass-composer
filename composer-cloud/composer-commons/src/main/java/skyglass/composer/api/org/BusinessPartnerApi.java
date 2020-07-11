package skyglass.composer.api.org;

import java.util.Collection;

import skyglass.composer.dto.org.BusinessPartnerDTO;
import skyglass.composer.dto.org.OrganizationalUnitDTO;

public interface BusinessPartnerApi {

	BusinessPartnerDTO createBusinessPartner(BusinessPartnerDTO dto);

	BusinessPartnerDTO getBusinessPartner(String businessPartnerUuid);

	Collection<OrganizationalUnitDTO> getAllOrganizationalUnitsForBusinessPartner(String businessPartnerUuid, Boolean belongToPlant);

}
