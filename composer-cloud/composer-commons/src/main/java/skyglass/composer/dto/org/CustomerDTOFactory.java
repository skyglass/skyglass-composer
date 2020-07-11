package skyglass.composer.dto.org;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import skyglass.composer.domain.businesspartner.BusinessCustomer;
import skyglass.composer.dto.AEntityDTOFactory;

public class CustomerDTOFactory extends AEntityDTOFactory {

	public static CustomerDTO createCustomerDTO(BusinessCustomer customer) {
		if (customer == null) {
			return null;
		}

		CustomerDTO dto = new CustomerDTO();
		dto.setUuid(customer.getUuid());
		dto.setBusinessPartner(BusinessPartnerDTOFactory.createBusinessPartnerDTO(customer.getBusinessPartner()));

		return dto;
	}

	public static List<CustomerDTO> createCustomerDTOs(Collection<BusinessCustomer> customers) {
		List<CustomerDTO> customerDTOs = new ArrayList<>();
		if (customers == null) {
			return customerDTOs;
		}

		for (BusinessCustomer customer : customers) {
			customerDTOs.add(createCustomerDTO(customer));
		}

		return customerDTOs;
	}

	public static CustomerDTO createBasicCustomerDTO(BusinessCustomer customer) {
		if (customer == null) {
			return null;
		}

		CustomerDTO dto = new CustomerDTO();
		dto.setUuid(customer.getUuid());
		dto.setBusinessPartner(BusinessPartnerDTOFactory.createBasicBusinessPartnerDTO(customer.getBusinessPartner()));

		return dto;
	}

	public static List<CustomerDTO> createBasicCustomerDTOs(Collection<BusinessCustomer> customers) {
		List<CustomerDTO> customerDTOs = new ArrayList<>();
		if (customers == null) {
			return customerDTOs;
		}

		for (BusinessCustomer customer : customers) {
			customerDTOs.add(createBasicCustomerDTO(customer));
		}

		return customerDTOs;
	}

	public static BusinessCustomer createCustomer(CustomerDTO dto) {
		if (dto == null) {
			return null;
		}
		BusinessCustomer customer = new BusinessCustomer();
		if (dto.getUuid() != null && !dto.getUuid().isEmpty()) {
			customer.setUuid(dto.getUuid());
		}
		customer.setBusinessPartner(BusinessPartnerDTOFactory.createBusinessPartner(dto.getBusinessPartner()));
		return customer;
	}

	public static List<BusinessCustomer> createCustomers(List<CustomerDTO> dtos) {
		List<BusinessCustomer> customers = new ArrayList<>();
		if (dtos == null) {
			return customers;
		}

		for (CustomerDTO dto : dtos) {
			customers.add(createCustomer(dto));
		}
		return customers;
	}

}
