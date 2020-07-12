package com.cakefactory.address.persistence;

import com.cakefactory.address.Address;
import com.cakefactory.address.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaAddressServiceTest {

    private static final String TEST_EMAIL = "test@xample.com";
    private static final String TEST_ADDRESS_LINE_1 = "line 1";
    private static final String TEST_ADDRESS_LINE_2 = "line 2";
    private static final String TEST_POSTCODE = "P1 ST";

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    AddressRepository addressRepository;

    AddressService addressService;

    @BeforeEach
    void setUp() {
        addressService = new JpaAddressService(addressRepository);
    }

    @Test
    void savesAddress() {
        addressService.update(TEST_EMAIL, TEST_ADDRESS_LINE_1, TEST_ADDRESS_LINE_2, TEST_POSTCODE);

        testEntityManager.flush();
        AddressEntity addressEntity = testEntityManager.find(AddressEntity.class, TEST_EMAIL);

        assertThat(addressEntity).isNotNull();
        assertThat(addressEntity.getAddressLine1()).isEqualTo(TEST_ADDRESS_LINE_1);
        assertThat(addressEntity.getAddressLine2()).isEqualTo(TEST_ADDRESS_LINE_2);
        assertThat(addressEntity.getPostcode()).isEqualTo(TEST_POSTCODE);
    }

    @Test
    void returnsAccount() {
        saveTestAddress();
        Address address = addressService.findOrEmpty(TEST_EMAIL);

        assertThat(address.getAddressLine1()).isEqualTo(TEST_ADDRESS_LINE_1);
        assertThat(address.getAddressLine2()).isEqualTo(TEST_ADDRESS_LINE_2);
        assertThat(address.getPostcode()).isEqualTo(TEST_POSTCODE);
    }

    @Test
    void returnsEmptyAddressForNonExistingAccount() {
        Address address = addressService.findOrEmpty(TEST_EMAIL);

        assertThat(address.getAddressLine1()).isEqualTo("");
        assertThat(address.getAddressLine2()).isEqualTo("");
        assertThat(address.getPostcode()).isEqualTo("");
    }

    private void saveTestAddress() {
        AddressEntity entity = new AddressEntity();
        entity.setEmail(TEST_EMAIL);
        entity.setAddressLine1(TEST_ADDRESS_LINE_1);
        entity.setAddressLine2(TEST_ADDRESS_LINE_2);
        entity.setPostcode(TEST_POSTCODE);

        testEntityManager.persistAndFlush(entity);
    }
}