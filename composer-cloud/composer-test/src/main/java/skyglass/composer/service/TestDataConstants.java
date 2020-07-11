package skyglass.composer.service;

import skyglass.composer.client.user.UserInfo;

public interface TestDataConstants {

	public static final String TEST_TIMEZONE = "Asia/Tokyo";

	public static final String TEST_USER_1_USERNAME = "USER1";

	static final UserInfo TEST_USER_1 = new UserInfo("USER1", "12345!Test", "skyglass", "testuser1", "skyglass-testuser1@skyglass.net");

	String BUSINESSPARTNER_1_UUID = "6018d2e1-b94b-424a-840c-9cbae9074f4e";

	String BUSINESSPARTNER_2_UUID = "befd22b7-53d0-4671-9df7-49dbbf38e45e";

	String BUSINESSPARTNER_1_NAME = "BusinessPartner1";

	String BUSINESSPARTNER_2_NAME = "BusinessPartner2";

	public static UserInfo getTestUser1() {
		return TEST_USER_1;
	}

}
