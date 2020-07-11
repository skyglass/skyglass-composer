package skyglass.composer.local.helper.security;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.local.bean.TestingApi;
import skyglass.composer.security.api.RoleApi;
import skyglass.composer.security.api.RolePermissionApi;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.dto.RoleDTO;
import skyglass.composer.security.dto.SecurityCacheRegistry;

public class RolePermissionLocalTestHelper {

	public static final String WORKPIECE_VIEWER_ROLE = "Workpiece Viewer";

	public static final String WORKPIECE_EDITOR_ROLE = "Workpiece Editor";

	public static final String GSM_VIEWER_ROLE = "GSM Viewer";

	public static final String GSM_EDITOR_ROLE = "GSM Editor";

	public static final String GSM_TAG_VIEWER_ROLE = "GSM Tag Viewer";

	public static final String GSM_TAG_EDITOR_ROLE = "GSM Tag Editor";

	public static final String GSM_LTD_VIEWER_ROLE = "GSM LTD Viewer";

	public static final String GSM_LTD_EDITOR_ROLE = "GSM LTD Editor";

	public static final String GSM_DOC_VIEWER_ROLE = "GSM Doc Viewer";

	public static final String GSM_DOC_EDITOR_ROLE = "GSM Doc Editor";

	private RoleApi roleApi;

	private RolePermissionApi rolePermissionApi;

	private TestingApi testingApi;

	public static RolePermissionLocalTestHelper create(RoleApi roleApi, RolePermissionApi rolePermissionApi, TestingApi testingApi) {
		return new RolePermissionLocalTestHelper(roleApi, rolePermissionApi, testingApi);
	}

	public RolePermissionLocalTestHelper(RoleApi roleApi, RolePermissionApi rolePermissionApi, TestingApi testingApi) {
		this.roleApi = roleApi;
		this.rolePermissionApi = rolePermissionApi;
		this.testingApi = testingApi;
	}

	public RoleDTO createRole(String name) {
		return createRole(name, null, null);
	}

	public RoleDTO createRole(String name, String parentUuid) {
		return createRole(name, parentUuid, null);
	}

	public RoleDTO createRole(String name, String parentUuid, Consumer<RoleDTO> consumer) {
		RoleDTO dto = createRoleDto(name, parentUuid, consumer);
		dto = roleApi.createRole(dto);
		if (StringUtils.isBlank(parentUuid)) {
			testingApi.addRootRoleHierarchyRecord(dto.getUuid(), dto.getName());
		} else {
			testingApi.addChildRoleHierarchyRecord(dto.getUuid(), parentUuid, dto.getName());
		}
		return dto;
	}

	public RoleDTO createRole(String name, String parentUuid, String grandParentUuid, Consumer<RoleDTO> consumer) {
		RoleDTO dto = createRoleDto(name, parentUuid, consumer);
		dto = roleApi.createRole(dto);
		testingApi.addGrandChildRoleHierarchyRecord(dto.getUuid(), parentUuid, grandParentUuid, dto.getName());
		return dto;
	}

	public RoleDTO createRoleDto(String name, String parentUuid, Consumer<RoleDTO> consumer) {
		RoleDTO dto = new RoleDTO();
		dto.setName(name);
		dto.setParentUuid(parentUuid);
		dto.setGlobal(true);
		if (consumer != null) {
			consumer.accept(dto);
		}
		return dto;
	}

	public void addRolePermission(String roleUuid, ResourceType resourceType, OperationType operationType) {
		Map<ResourceType, OperationType> map = new HashMap<>();
		map.put(resourceType, operationType);
		rolePermissionApi.addRolePermissions(roleUuid, map);
		SecurityCacheRegistry.close();
	}

	public void removeRolePermission(String roleUuid, ResourceType resourceType, OperationType operationType) {
		Map<ResourceType, OperationType> map = new HashMap<>();
		map.put(resourceType, operationType);
		rolePermissionApi.removeRolePermissions(roleUuid, map);
		SecurityCacheRegistry.close();
	}
}
