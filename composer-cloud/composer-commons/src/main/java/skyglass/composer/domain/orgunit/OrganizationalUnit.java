package skyglass.composer.domain.orgunit;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import skyglass.composer.domain.businesspartner.BusinessPartner;
import skyglass.composer.entity.AEntity;

@Entity
public class OrganizationalUnit extends AEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	private OrganizationalUnit parent;

	@ManyToOne
	private BusinessPartner businessPartner;

	@OneToMany(mappedBy = "parent")
	private List<OrganizationalUnit> childOrganizationalUnits = new ArrayList<>();

	@Column
	private String name;

	public OrganizationalUnit getParent() {
		return parent;
	}

	public void setParent(OrganizationalUnit parent) {
		this.parent = parent;
		if (parent != null) {
			parent.addChildOrganizationalUnit(this);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BusinessPartner getBusinessPartner() {
		return businessPartner;
	}

	public void setBusinessPartner(BusinessPartner businessPartner) {
		this.businessPartner = businessPartner;
	}

	public List<OrganizationalUnit> getChildOrganizationalUnits() {
		return childOrganizationalUnits;
	}

	public void setChildOrganizationalUnits(List<OrganizationalUnit> childOrganizationalUnits) {
		this.childOrganizationalUnits = childOrganizationalUnits;
	}

	public void addChildOrganizationalUnit(OrganizationalUnit organizationalUnit) {
		if (this.childOrganizationalUnits != null && !this.childOrganizationalUnits.contains(organizationalUnit)) {
			this.childOrganizationalUnits.add(organizationalUnit);
		}
	}
}
