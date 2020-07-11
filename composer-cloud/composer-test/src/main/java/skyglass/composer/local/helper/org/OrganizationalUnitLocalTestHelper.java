package skyglass.composer.local.helper.org;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyglass.composer.api.org.OrganizationalUnitApi;
import skyglass.composer.dto.org.BusinessPartnerDTO;
import skyglass.composer.dto.org.OrganizationalUnitDTO;
import skyglass.composer.local.bean.TestingApi;
import skyglass.composer.security.dto.UserDTO;

public class OrganizationalUnitLocalTestHelper {
	private static final Logger log = LoggerFactory.getLogger(OrganizationalUnitLocalTestHelper.class);

	private OrganizationalUnitApi organizationalUnitApi;

	private TestingApi testingApi;

	public static OrganizationalUnitLocalTestHelper create(OrganizationalUnitApi organizationalUnitApi, TestingApi testingApi) {
		return new OrganizationalUnitLocalTestHelper(organizationalUnitApi, testingApi);
	}

	public OrganizationalUnitLocalTestHelper(OrganizationalUnitApi organizationalUnitApi, TestingApi testingApi) {
		this.organizationalUnitApi = organizationalUnitApi;
		this.testingApi = testingApi;
	}

	public void deleteOrgUnit(OrganizationalUnitDTO createdOrganizationalUnit, UserDTO testUser, TestingApi testingApi) {
		log.debug(String.format("Deleting organizational unit data: %s", createdOrganizationalUnit.getUuid()));
		String orgUnitUuid = createdOrganizationalUnit.getUuid();
		String sqlString = "DELETE FROM USER_ORGANIZATIONALUNIT WHERE ORGANIZATIONALUNITS_UUID = '" + orgUnitUuid + "'"
				+ " AND USERS_UUID = '" + testUser.getUuid() + "'";
		testingApi.executeString(sqlString);
		sqlString = "DELETE FROM ORGANIZATIONALUNIT WHERE UUID = '" + orgUnitUuid + "'";
		testingApi.executeString(sqlString);
		log.debug(String.format("Deleted organizational unit data: %s", createdOrganizationalUnit.getUuid()));
	}

	public OrganizationalUnitDTO createOrganizationalUnit(BusinessPartnerDTO businessPartnerDto) {
		return createOrganizationalUnit(businessPartnerDto, null);
	}

	public OrganizationalUnitDTO createOrganizationalUnit(BusinessPartnerDTO businessPartnerDto,
			Consumer<OrganizationalUnitDTO> consumer) {
		return createOrganizationalUnit(businessPartnerDto, "ou-test", null, true, consumer);
	}

	public OrganizationalUnitDTO createOrganizationalUnit(BusinessPartnerDTO businessPartnerDTO, String name, String parentUuid) {
		return createOrganizationalUnit(businessPartnerDTO, name, parentUuid, false, null);
	}

	public OrganizationalUnitDTO createPlant(BusinessPartnerDTO businessPartnerDTO, String name, String parentUuid) {
		return createOrganizationalUnit(businessPartnerDTO, name, parentUuid, true, null);
	}

	public OrganizationalUnitDTO createOrganizationalUnit(BusinessPartnerDTO businessPartnerDTO, String name, String parentUuid, boolean isPlant, Consumer<OrganizationalUnitDTO> consumer) {
		OrganizationalUnitDTO createdOrganizationalUnit = createOrganizationalUnitDTO(businessPartnerDTO, consumer, name, parentUuid, isPlant);
		createdOrganizationalUnit = organizationalUnitApi.createOrganizationalUnit(createdOrganizationalUnit);
		if (StringUtils.isBlank(parentUuid)) {
			testingApi.addRootOrgUnitHierarchyRecord(createdOrganizationalUnit.getUuid(), businessPartnerDTO.getUuid());
		} else {
			testingApi.addChildOrgUnitHierarchyRecord(createdOrganizationalUnit.getUuid(), parentUuid, businessPartnerDTO.getUuid());
		}
		return createdOrganizationalUnit;
	}

	private OrganizationalUnitDTO createOrganizationalUnitDTO(BusinessPartnerDTO businessPartnerDto,
			Consumer<OrganizationalUnitDTO> consumer, String name, String parentUuid, boolean isPlant) {
		OrganizationalUnitDTO dto = new OrganizationalUnitDTO();
		dto.setName(name);
		dto.setBusinessPartnerUuid(businessPartnerDto.getUuid());
		dto.setParentUuid(parentUuid);
		if (consumer != null) {
			consumer.accept(dto);
		}
		return dto;
	}

}
