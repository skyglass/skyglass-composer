package skyglass.composer.dto;

import io.swagger.annotations.ApiModel;
import skyglass.composer.dto.org.BusinessPartnerDTO;
import skyglass.composer.dto.org.OrganizationalUnitDTO;

@ApiModel(parent = AEntityDTO.class, subTypes = { BusinessPartnerDTO.class, OrganizationalUnitDTO.class })
public class AEntityNameDTO extends AEntityDTO {
	private static final long serialVersionUID = 1L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
