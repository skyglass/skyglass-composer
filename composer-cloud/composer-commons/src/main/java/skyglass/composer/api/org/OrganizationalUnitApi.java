package skyglass.composer.api.org;

import skyglass.composer.dto.org.OrganizationalUnitDTO;

public interface OrganizationalUnitApi {

	OrganizationalUnitDTO getOrganizationalUnit(String orgUnitUuid);

	OrganizationalUnitDTO createOrganizationalUnit(OrganizationalUnitDTO dto);

}
