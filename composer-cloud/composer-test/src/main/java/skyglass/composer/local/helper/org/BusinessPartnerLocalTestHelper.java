package skyglass.composer.local.helper.org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyglass.composer.api.org.BusinessPartnerApi;
import skyglass.composer.dto.org.BusinessPartnerDTO;
import skyglass.composer.local.bean.TestingApi;
import skyglass.composer.security.dto.UserDTO;
import skyglass.composer.service.TestDataConstants;

public class BusinessPartnerLocalTestHelper {
	private static final Logger log = LoggerFactory.getLogger(BusinessPartnerLocalTestHelper.class);

	private BusinessPartnerApi businessPartnerApi;

	public static BusinessPartnerLocalTestHelper create(BusinessPartnerApi businessPartnerApi) {
		return new BusinessPartnerLocalTestHelper(businessPartnerApi);
	}

	public BusinessPartnerLocalTestHelper(BusinessPartnerApi businessPartnerApi) {
		this.businessPartnerApi = businessPartnerApi;
	}

	public void deleteBusinessPartner(BusinessPartnerDTO createdBusinessPartner, TestingApi testingApi, UserDTO testUser) {
		log.debug(String.format("Deleting business partner test data: %s", createdBusinessPartner.getUuid()));
		String businessPartnerUuid = createdBusinessPartner.getUuid();
		String sqlString = "DELETE FROM USER_BUSINESSPARTNER WHERE BUSINESSPARTNERS_UUID = '" + businessPartnerUuid
				+ "'" + " AND USERS_UUID = '" + testUser.getUuid() + "'";
		testingApi.executeString(sqlString);

		sqlString = "DELETE FROM BUSINESSPARTNER WHERE UUID = '" + businessPartnerUuid + "'";
		testingApi.executeString(sqlString);
		log.debug(String.format("Deleted business partner test data: %s", createdBusinessPartner.getUuid()));
	}

	public BusinessPartnerDTO getBusinessPartner() {
		return businessPartnerApi.getBusinessPartner(TestDataConstants.BUSINESSPARTNER_1_UUID);
	}

	public BusinessPartnerDTO createBasicBusinessPartner(String name) {
		BusinessPartnerDTO dto = new BusinessPartnerDTO();
		dto.setName(name);
		return businessPartnerApi.createBusinessPartner(dto);
	}
}
