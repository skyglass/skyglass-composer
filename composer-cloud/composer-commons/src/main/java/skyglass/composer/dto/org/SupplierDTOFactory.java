package skyglass.composer.dto.org;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import skyglass.composer.domain.businesspartner.Supplier;
import skyglass.composer.dto.AEntityDTOFactory;

public class SupplierDTOFactory extends AEntityDTOFactory {

	public static SupplierDTO createSupplierDTO(Supplier supplier) {
		if (supplier == null) {
			return null;
		}
		SupplierDTO dto = new SupplierDTO();
		dto.setUuid(supplier.getUuid());
		dto.setBusinessPartner(
				BusinessPartnerDTOFactory.createBusinessPartnerDTOWithoutOrgUnits(supplier.getBusinessPartner()));
		return dto;
	}

	public static List<SupplierDTO> createSupplierDTOs(Collection<Supplier> suppliers) {
		List<SupplierDTO> supplierDTOs = new ArrayList<>();
		if (suppliers == null) {
			return supplierDTOs;
		}

		for (Supplier supplier : suppliers) {
			supplierDTOs.add(createSupplierDTO(supplier));
		}

		return supplierDTOs;
	}

	public static SupplierDTO createBasicSupplierDTO(Supplier supplier) {
		if (supplier == null) {
			return null;
		}

		SupplierDTO dto = createVeryBasicDto(supplier, () -> new SupplierDTO());
		dto.setBusinessPartner(BusinessPartnerDTOFactory.createBasicBusinessPartnerDTO(supplier.getBusinessPartner()));

		return dto;
	}

	public static List<SupplierDTO> createBasicSupplierDTOs(Collection<Supplier> suppliers) {
		List<SupplierDTO> supplierDTOs = new ArrayList<>();
		if (suppliers == null) {
			return supplierDTOs;
		}

		for (Supplier supplier : suppliers) {
			supplierDTOs.add(createBasicSupplierDTO(supplier));
		}

		return supplierDTOs;
	}

	public static Supplier createSupplier(SupplierDTO dto) {
		if (dto == null) {
			return null;
		}
		Supplier supplier = new Supplier();
		if (dto.getUuid() != null && !dto.getUuid().isEmpty()) {
			supplier.setUuid(dto.getUuid());
		}
		supplier.setBusinessPartner(BusinessPartnerDTOFactory.createBusinessPartner(dto.getBusinessPartner()));
		return supplier;
	}

	public static List<Supplier> createSuppliers(List<SupplierDTO> dtos) {
		List<Supplier> suppliers = new ArrayList<>();
		if (dtos == null) {
			return suppliers;
		}

		for (SupplierDTO dto : dtos) {
			suppliers.add(createSupplier(dto));
		}
		return suppliers;
	}

}
