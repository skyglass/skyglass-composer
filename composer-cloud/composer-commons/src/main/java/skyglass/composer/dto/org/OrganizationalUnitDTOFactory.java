package skyglass.composer.dto.org;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import skyglass.composer.domain.businesspartner.BusinessPartner;
import skyglass.composer.domain.orgunit.OrganizationalUnit;
import skyglass.composer.dto.AEntityDTOFactory;

public class OrganizationalUnitDTOFactory extends AEntityDTOFactory {

	public static OrganizationalUnitDTO createOrganizationalUnitDTO(OrganizationalUnit orgUnit) {
		return createRecursiveOrganizationalUnitDTO(orgUnit, new HashMap<>());
	}

	public static OrganizationalUnitDTO createSimpleOrganizationalUnitDTO(OrganizationalUnit orgUnit) {
		if (orgUnit == null) {
			return null;
		}
		OrganizationalUnitDTO dto = new OrganizationalUnitDTO();
		dto.setUuid(orgUnit.getUuid());
		dto.setName(orgUnit.getName());
		if (orgUnit.getBusinessPartner() != null) {
			dto.setBusinessPartnerUuid(orgUnit.getBusinessPartner().getUuid());
		}

		if (orgUnit.getParent() != null) {
			dto.setParentUuid(orgUnit.getParent().getUuid());
			dto.setParentName(orgUnit.getParent().getName());
		}

		return dto;
	}

	public static List<OrganizationalUnitDTO> createSimpleOrganizationalUnitDTOs(List<OrganizationalUnit> orgUnits) {
		if (orgUnits == null) {
			return new ArrayList<>();
		}

		return orgUnits.stream().map(orgUnit -> createSimpleOrganizationalUnitDTO(orgUnit)).collect(Collectors.toList());
	}

	private static OrganizationalUnitDTO createRecursiveOrganizationalUnitDTO(OrganizationalUnit orgUnit,
			Map<String, Boolean> alreadyProcessed) {
		if (orgUnit == null) {
			return null;
		}
		OrganizationalUnitDTO dto = new OrganizationalUnitDTO();
		initializedOrganizationUnitDTO(orgUnit, alreadyProcessed, dto);
		return dto;
	}

	private static void initializedOrganizationUnitDTO(OrganizationalUnit orgUnit,
			Map<String, Boolean> alreadyProcessed, OrganizationalUnitDTO dto) {
		dto.setUuid(orgUnit.getUuid());
		dto.setName(orgUnit.getName());
		if (orgUnit.getBusinessPartner() != null) {
			dto.setBusinessPartnerUuid(orgUnit.getBusinessPartner().getUuid());
		}

		if (orgUnit.getParent() != null) {
			dto.setParentUuid(orgUnit.getParent().getUuid());
			dto.setParentName(orgUnit.getParent().getName());
		}

		handleRecursiveChildOrganizations(orgUnit, dto, alreadyProcessed);
	}

	private static void handleRecursiveChildOrganizations(OrganizationalUnit orgUnit, OrganizationalUnitDTO dto,
			Map<String, Boolean> alreadyProcessed) {
		if (alreadyProcessed.containsKey(orgUnit.getUuid()) && alreadyProcessed.get(orgUnit.getUuid())) {
			return;
		}
		if (orgUnit.getChildOrganizationalUnits() != null) {
			List<OrganizationalUnitDTO> orgUnits = new ArrayList<>();
			for (OrganizationalUnit childOrgUnit : orgUnit.getChildOrganizationalUnits()) {
				orgUnits.add(OrganizationalUnitDTOFactory.createRecursiveOrganizationalUnitDTO(childOrgUnit,
						alreadyProcessed));
			}
			alreadyProcessed.put(orgUnit.getUuid(), true);
			dto.setChildOrganizationalUnits(orgUnits);
		} else {
			alreadyProcessed.put(orgUnit.getUuid(), true);
		}

	}

	public static List<OrganizationalUnitDTO> buildDTOHierarchy(Collection<OrganizationalUnit> units,
			BusinessPartnerDTO businessPartnerDto) {
		List<OrganizationalUnitDTO> topLevel = new ArrayList<>();

		for (OrganizationalUnit ou : units) {
			if (ou.getParent() == null && ou.getBusinessPartner() != null
					&& ou.getBusinessPartner().getUuid().equals(businessPartnerDto.getUuid())) {
				// top level DTO
				OrganizationalUnitDTO ouDto = new OrganizationalUnitDTO();
				ouDto.setUuid(ou.getUuid());
				ouDto.setName(ou.getName());

				ouDto.setBusinessPartnerUuid(businessPartnerDto.getUuid());

				ouDto.setChildOrganizationalUnits(new ArrayList<>());

				topLevel.add(ouDto);
			}
		}

		buildOrgUnitHierarchy(topLevel, units);

		return topLevel;
	}

	private static void buildOrgUnitHierarchy(Collection<OrganizationalUnitDTO> topLevel,
			Collection<OrganizationalUnit> units) {

		if (topLevel == null || topLevel.isEmpty()) {
			return;
		}

		List<OrganizationalUnitDTO> nextLevel = new ArrayList<>();

		for (OrganizationalUnitDTO ou : topLevel) {
			for (OrganizationalUnit potentialChild : units) {
				if (potentialChild.getParent() == null || potentialChild.getUuid().equals(ou.getUuid())) {
					continue;
				}
				if (potentialChild.getParent().getUuid().equals(ou.getUuid())) {

					OrganizationalUnitDTO ouDto = new OrganizationalUnitDTO();
					ouDto.setUuid(potentialChild.getUuid());
					ouDto.setName(potentialChild.getName());

					ouDto.setParentUuid(ou.getUuid());
					ouDto.setParentName(ou.getName());
					ouDto.setChildOrganizationalUnits(new ArrayList<>());

					nextLevel.add(ouDto);

					ou.getChildOrganizationalUnits().add(ouDto);
				}
			}
		}

		buildOrgUnitHierarchy(nextLevel, units);
	}

	public static OrganizationalUnit createOrganizationalUnit(OrganizationalUnitDTO dto) {
		if (dto == null) {
			return null;
		}
		OrganizationalUnit orgUnit = new OrganizationalUnit();
		if (dto.getUuid() != null && !dto.getUuid().isEmpty()) {
			orgUnit.setUuid(dto.getUuid());
		}
		orgUnit.setName(dto.getName());

		if (dto.getBusinessPartnerUuid() != null) {
			BusinessPartner businessPartner = new BusinessPartner();
			businessPartner.setUuid(dto.getBusinessPartnerUuid());
			orgUnit.setBusinessPartner(businessPartner);
		}
		if (dto.getParentUuid() != null) {
			OrganizationalUnit parentOrgUnit = new OrganizationalUnit();
			parentOrgUnit.setUuid(dto.getParentUuid());
			orgUnit.setParent(parentOrgUnit);
		}

		return orgUnit;
	}

	public static List<OrganizationalUnit> createOrganizationalUnits(List<OrganizationalUnitDTO> orgUnitDtos) {
		if (orgUnitDtos == null) {
			return new ArrayList<>();
		}

		return orgUnitDtos.stream().map(orgUnitDto -> createOrganizationalUnit(orgUnitDto)).collect(Collectors.toList());
	}

	public static OrganizationalUnitWithBusinessPartnerDTO createOrganizationalUnitWithBusinessPartnerDTO(
			OrganizationalUnit orgUnit) {
		return createRecursiveOrganizationalUnitWithBusinessPartnerDTO(orgUnit, new HashMap<>());
	}

	private static OrganizationalUnitWithBusinessPartnerDTO createRecursiveOrganizationalUnitWithBusinessPartnerDTO(
			OrganizationalUnit orgUnit, Map<String, Boolean> alreadyProcessed) {
		if (orgUnit == null) {
			return null;
		}
		OrganizationalUnitWithBusinessPartnerDTO dto = new OrganizationalUnitWithBusinessPartnerDTO();
		initializedOrganizationUnitDTO(orgUnit, alreadyProcessed, dto);

		if (orgUnit.getBusinessPartner() != null) {
			dto.setBusinessPartnerName(orgUnit.getBusinessPartner().getName());
		}

		return dto;
	}

}
