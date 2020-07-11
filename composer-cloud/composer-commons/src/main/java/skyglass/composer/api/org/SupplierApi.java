package skyglass.composer.api.org;

import skyglass.composer.dto.org.SupplierDTO;

public interface SupplierApi {

	SupplierDTO getSupplier(String supplierUuid);

	SupplierDTO createSupplier(SupplierDTO supplierDto);
}
