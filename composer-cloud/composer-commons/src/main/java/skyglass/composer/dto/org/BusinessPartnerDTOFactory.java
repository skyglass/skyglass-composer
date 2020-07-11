package skyglass.composer.dto.org;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.domain.businesspartner.BusinessPartner;
import skyglass.composer.domain.orgunit.OrganizationalUnit;
import skyglass.composer.dto.AEntityDTOFactory;

public class BusinessPartnerDTOFactory extends AEntityDTOFactory {

	public static BusinessPartnerDTO createBusinessPartnerDTO(BusinessPartner businessPartner) {
		if (businessPartner == null) {
			return null;
		}

		BusinessPartnerDTO dto = createBusinessPartnerDTOWithoutOrgUnits(businessPartner);
		if (businessPartner.getOrganizationalUnits() != null) {
			List<OrganizationalUnitDTO> orgUnits = new ArrayList<>();
			for (OrganizationalUnit orgUnit : businessPartner.getOrganizationalUnits()) {
				BusinessPartner currentBusinessPartner = orgUnit.getBusinessPartner();
				orgUnit.setBusinessPartner(null);
				OrganizationalUnitDTO ouDto = OrganizationalUnitDTOFactory.createOrganizationalUnitDTO(orgUnit);
				orgUnit.setBusinessPartner(currentBusinessPartner);
				orgUnits.add(ouDto);
			}
			dto.setOrganizationalUnits(orgUnits);
		}
		return dto;
	}

	public static BusinessPartnerDTO createBasicBusinessPartnerDTO(BusinessPartner businessPartner) {
		if (businessPartner == null) {
			return null;
		}
		BusinessPartnerDTO dto = createVeryBasicDto(businessPartner, BusinessPartnerDTO::new);
		dto.setName(businessPartner.getName());
		return dto;
	}

	public static List<BusinessPartnerDTO> createBasicBusinessPartnerDTOs(Collection<BusinessPartner> businessPartners) {
		return businessPartners.stream().map(BusinessPartnerDTOFactory::createBasicBusinessPartnerDTO)
				.sorted((dto, other) -> StringUtils.compare(StringUtils.lowerCase(dto.getName()), StringUtils.lowerCase(other.getName()))).collect(Collectors.toList());
	}

	public static BusinessPartnerDTO createBusinessPartnerDTOWithoutOrgUnits(BusinessPartner businessPartner) {
		if (businessPartner == null) {
			return null;
		}

		BusinessPartnerDTO dto = new BusinessPartnerDTO();
		dto.setName(businessPartner.getName());
		dto.setUuid(businessPartner.getUuid());
		dto.setParentBusinessPartnerUuid(provideUuidFromReference(businessPartner.getParentBusinessPartner()));
		dto.setSupplierUuid(provideUuidFromReference(businessPartner.getSupplier()));
		dto.setCustomerUuid(provideUuidFromReference(businessPartner.getCustomer()));

		return dto;
	}

	public static BusinessPartner createBusinessPartner(BusinessPartnerDTO dto) {
		if (dto == null) {
			return null;
		}

		BusinessPartner businessPartner = new BusinessPartner();
		if (StringUtils.isNotBlank(dto.getUuid())) {
			businessPartner.setUuid(dto.getUuid());
		}

		businessPartner.setName(dto.getName());
		businessPartner.setParentBusinessPartner(createVeryBasicEntity(dto.getParentBusinessPartnerUuid(), () -> new BusinessPartner()));

		if (dto.getOrganizationalUnits() != null) {
			List<OrganizationalUnit> orgUnits = new ArrayList<>();
			for (OrganizationalUnitDTO orgUnitDto : dto.getOrganizationalUnits()) {
				orgUnits.add(OrganizationalUnitDTOFactory.createOrganizationalUnit(orgUnitDto));
			}

			businessPartner.setOrganizationalUnits(orgUnits);
		}

		return businessPartner;
	}

}
