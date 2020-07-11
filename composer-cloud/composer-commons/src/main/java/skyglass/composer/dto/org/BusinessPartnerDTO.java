package skyglass.composer.dto.org;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import skyglass.composer.dto.AEntityNameDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessPartnerDTO extends AEntityNameDTO {

	private static final long serialVersionUID = 1L;

	private String parentBusinessPartnerUuid;

	private String supplierUuid;

	private String customerUuid;

	private List<OrganizationalUnitDTO> organizationalUnits;

	private String dunsNumber;

	private String vatNumber;

	private String registrationNumber;

	private String registrationNotes;

	private String billingContact;

	public BusinessPartnerDTO() {
		organizationalUnits = new ArrayList<>();
	}

	public List<OrganizationalUnitDTO> getOrganizationalUnits() {
		return organizationalUnits;
	}

	public void setOrganizationalUnits(List<OrganizationalUnitDTO> organizationalUnits) {
		this.organizationalUnits = organizationalUnits;
	}

	public String getParentBusinessPartnerUuid() {
		return parentBusinessPartnerUuid;
	}

	public void setParentBusinessPartnerUuid(String parentBusinessPartnerUuid) {
		this.parentBusinessPartnerUuid = parentBusinessPartnerUuid;
	}

	public String getSupplierUuid() {
		return supplierUuid;
	}

	public void setSupplierUuid(String supplierUuid) {
		this.supplierUuid = supplierUuid;
	}

	public String getCustomerUuid() {
		return customerUuid;
	}

	public void setCustomerUuid(String customerUuid) {
		this.customerUuid = customerUuid;
	}

	public String getDunsNumber() {
		return dunsNumber;
	}

	public void setDunsNumber(String dunsNumber) {
		this.dunsNumber = dunsNumber;
	}

	public String getVatNumber() {
		return vatNumber;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getRegistrationNotes() {
		return registrationNotes;
	}

	public void setRegistrationNotes(String registrationNotes) {
		this.registrationNotes = registrationNotes;
	}

	public String getBillingContact() {
		return billingContact;
	}

	public void setBillingContact(String billingContact) {
		this.billingContact = billingContact;
	}

}
