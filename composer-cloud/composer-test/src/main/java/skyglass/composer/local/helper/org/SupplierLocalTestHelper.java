package skyglass.composer.local.helper.org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyglass.composer.api.org.SupplierApi;
import skyglass.composer.dto.org.BusinessPartnerDTO;
import skyglass.composer.dto.org.SupplierDTO;
import skyglass.composer.local.bean.TestingApi;

public class SupplierLocalTestHelper {

	private static final Logger log = LoggerFactory.getLogger(SupplierLocalTestHelper.class);

	private SupplierApi supplierApi;

	public static SupplierLocalTestHelper create(SupplierApi supplierApi) {
		return new SupplierLocalTestHelper(supplierApi);
	}

	public SupplierLocalTestHelper(SupplierApi supplierApi) {
		this.supplierApi = supplierApi;
	}

	public SupplierDTO createSupplier(BusinessPartnerDTO bpDto) {
		SupplierDTO dto = createSupplierDTO(bpDto);
		return supplierApi.createSupplier(dto);
	}

	public void deleteSupplier(SupplierDTO createdSupplier, TestingApi testingApi) {
		log.debug(String.format("Deleting supplier data: %s", createdSupplier.getUuid()));
		String sqlString = "DELETE FROM SUPPLIER WHERE UUID = '" + createdSupplier.getUuid() + "'";
		testingApi.executeString(sqlString);
		log.debug(String.format("Deleted supplier data: %s", createdSupplier.getUuid()));
	}

	public SupplierDTO getSupplier(String uuid) {
		return supplierApi.getSupplier(uuid);
	}

	private SupplierDTO createSupplierDTO(BusinessPartnerDTO bpDto) {
		SupplierDTO dto = new SupplierDTO();
		dto.setBusinessPartner(bpDto);
		return dto;
	}

}
