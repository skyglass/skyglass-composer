package skyglass.composer.dto.org;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationalUnitWithBusinessPartnerDTO extends OrganizationalUnitDTO {

	private static final long serialVersionUID = 1L;

	private String businessPartnerName;

	public String getBusinessPartnerName() {
		return businessPartnerName;
	}

	public void setBusinessPartnerName(String businessPartnerName) {
		this.businessPartnerName = businessPartnerName;
	}

}
