package skyglass.composer.dto.org;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import skyglass.composer.dto.AEntityDTO;

@ApiModel(parent = AEntityDTO.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDTO extends AEntityDTO {

	private static final long serialVersionUID = 1L;

	private BusinessPartnerDTO businessPartner;

	public BusinessPartnerDTO getBusinessPartner() {
		return businessPartner;
	}

	public void setBusinessPartner(BusinessPartnerDTO businessPartner) {
		this.businessPartner = businessPartner;
	}
}
