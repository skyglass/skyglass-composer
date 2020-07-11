package skyglass.composer.local.bean;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import skyglass.composer.security.dto.RoleDTO;

@Component
public class TestingApi {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void executeScript(File file) throws IOException {
		String sqlScript = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		jdbcTemplate.execute(sqlScript);
	}

	public void executeString(String sql) {
		jdbcTemplate.execute(sql);
	}

	public void addRootOrgUnitHierarchyRecord(String childUuid, String businessPartnerUuid) {
		if (checkH2Database()) {
			jdbcTemplate.execute(String.format(
					"insert into ORGUNIT_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID) values ('1', '%s', '%s')", childUuid, childUuid));
		}
	}

	public void addChildOrgUnitHierarchyRecord(String childUuid, String parentUuid, String businessPartnerUuid) {
		if (checkH2Database()) {
			jdbcTemplate.execute(String.format(
					"insert into ORGUNIT_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID) values ('1', '%s', '%s')", childUuid, childUuid));
			jdbcTemplate.execute(String.format(
					"insert into ORGUNIT_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID) values ('1', '%s', '%s')", childUuid, parentUuid));
		}
	}

	public void addRootRoleHierarchyRecord(String childUuid, String name) {
		if (checkH2Database()) {
			jdbcTemplate.execute(String.format(
					"insert into ROLE_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID, NAME) values ('1', '%s', '%s', '%s')", childUuid, childUuid, name));
		}
	}

	public void addChildRoleHierarchyRecord(String childUuid, String parentUuid, String name) {
		if (checkH2Database()) {
			jdbcTemplate.execute(String.format(
					"insert into ROLE_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID, NAME) values ('1', '%s', '%s', '%s')", childUuid, childUuid, name));
			jdbcTemplate.execute(String.format(
					"insert into ROLE_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID, NAME) values ('1', '%s', '%s', '%s')", childUuid, parentUuid, name));
		}
	}

	public void addGrandGrandChildRoleHierarchyRecord(String grandGrandChildUuid, String grandChildUuid, String childUuid, String parentUuid, String name) {
		if (checkH2Database()) {
			jdbcTemplate.execute(String.format(
					"insert into ROLE_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID, NAME) values ('1', '%s', '%s', '%s')", grandGrandChildUuid, grandGrandChildUuid,
					name));
			jdbcTemplate.execute(String.format(
					"insert into ROLE_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID, NAME) values ('1', '%s', '%s', '%s')", grandGrandChildUuid, grandChildUuid,
					name));
			jdbcTemplate.execute(String.format(
					"insert into ROLE_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID, NAME) values ('1', '%s', '%s', '%s')", grandGrandChildUuid, childUuid,
					name));
			jdbcTemplate.execute(String.format(
					"insert into ROLE_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID, NAME) values ('1', '%s', '%s', '%s')", grandGrandChildUuid, parentUuid,
					name));

		}
	}

	public void addGrandChildRoleHierarchyRecord(String grandChildUuid, String childUuid, String parentUuid, String name) {
		if (checkH2Database()) {
			jdbcTemplate.execute(String.format(
					"insert into ROLE_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID, NAME) values ('1', '%s', '%s', '%s')", grandChildUuid, grandChildUuid,
					name));
			jdbcTemplate.execute(String.format(
					"insert into ROLE_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID, NAME) values ('1', '%s', '%s', '%s')", grandChildUuid, childUuid,
					name));
			jdbcTemplate.execute(String.format(
					"insert into ROLE_HIERARCHYVIEW (UUID, CHILD_UUID, PARENT_UUID, NAME) values ('1', '%s', '%s', '%s')", grandChildUuid, parentUuid,
					name));
		}
	}

	public void resetRoleHierarchy(RoleDTO parent, RoleDTO child, RoleDTO grandChild, RoleDTO grandGrandChild) {
		if (checkH2Database()) {
			jdbcTemplate.execute(String.format("delete from ROLE_HIERARCHYVIEW WHERE CHILD_UUID = '%s'", parent.getUuid()));
			jdbcTemplate.execute(String.format("delete from ROLE_HIERARCHYVIEW WHERE CHILD_UUID = '%s'", child.getUuid()));
			jdbcTemplate.execute(String.format("delete from ROLE_HIERARCHYVIEW WHERE CHILD_UUID = '%s'", grandChild.getUuid()));
			jdbcTemplate.execute(String.format("delete from ROLE_HIERARCHYVIEW WHERE CHILD_UUID = '%s'", grandChild.getUuid()));
			addRootRoleHierarchyRecord(parent.getUuid(), parent.getName());
			addChildRoleHierarchyRecord(child.getUuid(), parent.getUuid(), child.getName());
			addGrandChildRoleHierarchyRecord(grandChild.getUuid(), child.getUuid(), parent.getUuid(), grandChild.getName());
			addGrandGrandChildRoleHierarchyRecord(grandGrandChild.getUuid(), grandChild.getUuid(), child.getUuid(), parent.getUuid(), grandGrandChild.getName());
		}
	}

	private boolean checkH2Database() throws IllegalStateException, UnsupportedOperationException {
		try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			return StringUtils.containsIgnoreCase(metaData.getDriverName(), "h2");
		} catch (SQLException ex) {
			throw new IllegalStateException("Could not establish database connection", ex);
		}
	}
}
