package skyglass.composer.security.service;

import skyglass.composer.domain.IIdentifiable;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;

public class SecurityCheckHelper {

	public static String getResourceParentUuid(IIdentifiable idEntity) {
		return getResourceParentUuid(idEntity, null, true);
	}

	public static String getResourceParentUuid(IIdentifiable idEntity, boolean throwException) {
		return getResourceParentUuid(idEntity, null, throwException);
	}

	public static String getResourceParentUuid(IIdentifiable idEntity, ResourceType resourceType) {
		return getResourceParentUuid(idEntity, resourceType, true);
	}

	public static String getResourceParentUuid(IIdentifiable idEntity, ResourceType resourceType, boolean throwException) {
		try {
			return idEntity.getUuid();
		} catch (IllegalArgumentException e) {
			if (throwException) {
				throw e;
			}
			return null;
		}
	}

	public static String jpa(String alias, ResourceType resourceType) {
		return jpa(alias, resourceType, OperationType.Read);
	}

	public static String jpa(String alias, ResourceType resourceType, OperationType operationType) {
		return _sql(alias, resourceType, operationType, false);
	}

	public static String nativ(String alias, ResourceType resourceType) {
		return nativ(alias, resourceType, OperationType.Read);
	}

	public static String nativ(String alias, ResourceType resourceType, OperationType operationType) {
		return _sql(alias, resourceType, operationType, true);
	}

	private static String _sql(String alias, ResourceType resourceType, OperationType operationType, boolean nativ) {
		return String.format("JOIN "
				+ (nativ ? "USERRESOURCECONTEXTPERMISSIONVIEW" : "UserResourceContextPermissionView") + " auth "
				+ "ON auth.user_uuid = " + (nativ ? "?userUuid " : ":userUuid ")
				+ "AND auth.resource_uuid = %s.uuid "
				+ "AND auth.resourceType = '%s' AND auth.operation_name = '%s'",
				alias, resourceType.toString(), operationType.toString());
	}

}
