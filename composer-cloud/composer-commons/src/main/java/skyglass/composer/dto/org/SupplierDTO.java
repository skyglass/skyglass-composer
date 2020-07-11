package skyglass.composer.dto.org;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import skyglass.composer.dto.AEntityDTO;

@ApiModel(parent = AEntityDTO.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierDTO extends AEntityDTO {
	private static final long serialVersionUID = 1L;

	private String primaryEmail;

	private BusinessPartnerDTO businessPartner;

	public String getPrimaryEmail() {
		return primaryEmail;
	}

	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}

	public BusinessPartnerDTO getBusinessPartner() {
		return businessPartner;
	}

	public void setBusinessPartner(BusinessPartnerDTO businessPartner) {
		this.businessPartner = businessPartner;
	}

}
