package skyglass.composer.api.org;

import skyglass.composer.dto.org.CustomerDTO;

public interface CustomerApi {

	CustomerDTO getCustomer(String customerUuid);

	CustomerDTO createCustomer(CustomerDTO customerDto);
}
