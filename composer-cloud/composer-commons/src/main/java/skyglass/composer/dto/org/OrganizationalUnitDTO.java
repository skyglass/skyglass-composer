package skyglass.composer.dto.org;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import skyglass.composer.dto.AEntityNameDTO;

@ApiModel(subTypes = { OrganizationalUnitWithBusinessPartnerDTO.class })
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationalUnitDTO extends AEntityNameDTO {

	private static final long serialVersionUID = 1L;

	private String parentUuid;

	private String parentName;

	private String businessPartnerUuid;

	private List<OrganizationalUnitDTO> childOrganizationalUnits = new ArrayList<>();

	public List<OrganizationalUnitDTO> getChildOrganizationalUnits() {
		return childOrganizationalUnits;
	}

	public void setChildOrganizationalUnits(List<OrganizationalUnitDTO> childOrganizationalUnits) {
		this.childOrganizationalUnits = childOrganizationalUnits;
	}

	public String getBusinessPartnerUuid() {
		return businessPartnerUuid;
	}

	public void setBusinessPartnerUuid(String businessPartnerUuid) {
		this.businessPartnerUuid = businessPartnerUuid;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

}
