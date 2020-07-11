package skyglass.composer.domain.businesspartner;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import skyglass.composer.domain.orgunit.OrganizationalUnit;
import skyglass.composer.entity.AEntity;

@Entity
public class BusinessPartner extends AEntity {

	private static final long serialVersionUID = 1L;

	@Column
	private String name;

	@ManyToOne
	private BusinessPartner parentBusinessPartner;

	@ManyToOne
	private BusinessCustomer customer;

	@ManyToOne
	private Supplier supplier;

	@OneToMany(mappedBy = "parentBusinessPartner")
	private List<BusinessPartner> childBusinessPartners;

	@OneToMany(mappedBy = "businessPartner")
	private List<OrganizationalUnit> organizationalUnits = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BusinessPartner getParentBusinessPartner() {
		return parentBusinessPartner;
	}

	public void setParentBusinessPartner(BusinessPartner parentBusinessPartner) {
		this.parentBusinessPartner = parentBusinessPartner;
	}

	public List<OrganizationalUnit> getOrganizationalUnits() {
		return organizationalUnits;
	}

	public void setOrganizationalUnits(List<OrganizationalUnit> organizationalUnits) {
		this.organizationalUnits = organizationalUnits;
		// JPA setter
		if (organizationalUnits != null) {
			for (OrganizationalUnit unit : organizationalUnits) {
				unit.setBusinessPartner(this);
			}
		}
	}

	public List<BusinessPartner> getChildBusinessPartners() {
		return childBusinessPartners;
	}

	public void addOrgUnit(OrganizationalUnit organizationalUnit) {
		if (this.organizationalUnits != null && !this.organizationalUnits.contains(organizationalUnit)) {
			this.organizationalUnits.add(organizationalUnit);
		}
	}

	public BusinessCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(BusinessCustomer customer) {
		this.customer = customer;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

}
