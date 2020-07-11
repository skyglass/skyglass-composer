package skyglass.composer.local.helper.org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyglass.composer.api.org.CustomerApi;
import skyglass.composer.dto.org.BusinessPartnerDTO;
import skyglass.composer.dto.org.CustomerDTO;
import skyglass.composer.local.bean.TestingApi;

public class CustomerLocalTestHelper {
	private static final Logger log = LoggerFactory.getLogger(CustomerLocalTestHelper.class);

	private CustomerApi customerApi;

	public static CustomerLocalTestHelper create(CustomerApi customerApi) {
		return new CustomerLocalTestHelper(customerApi);
	}

	public CustomerLocalTestHelper(CustomerApi customerApi) {
		this.customerApi = customerApi;
	}

	public CustomerDTO createCustomer(BusinessPartnerDTO bpDto) {
		CustomerDTO dto = createCustomerDTO(bpDto);
		return customerApi.createCustomer(dto);
	}

	public void deleteCustomer(CustomerDTO createdCustomer, TestingApi testingApi) {
		log.debug(String.format("Deleting customer data: %s", createdCustomer.getUuid()));
		String sqlString = "DELETE FROM CUSTOMER WHERE UUID = '" + createdCustomer.getUuid() + "'";
		testingApi.executeString(sqlString);
		log.debug(String.format("Deleted customer data: %s", createdCustomer.getUuid()));
	}

	public CustomerDTO getCustomer(String uuid) {
		return customerApi.getCustomer(uuid);
	}

	private CustomerDTO createCustomerDTO(BusinessPartnerDTO bpDto) {
		CustomerDTO dto = new CustomerDTO();
		dto.setBusinessPartner(bpDto);
		return dto;
	}

}
