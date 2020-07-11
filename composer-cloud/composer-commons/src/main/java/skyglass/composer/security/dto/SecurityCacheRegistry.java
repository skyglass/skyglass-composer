package skyglass.composer.security.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;

/**
 * The security cache registry is used to retrieve and cache security checks for the current user
 *
 * NOTE: This class is designed to be used in single threaded environments
 * because it makes use of {@link ThreadLocal} and therefore respective
 * {@link Thread#currentThread}.
 * 
 * @author skyglass
 */
public class SecurityCacheRegistry {

	private static final ThreadLocal<SecurityCacheRegistry> threadLocal = new ThreadLocal<>();

	private Map<SecurityResourceCacheId, Optional<Boolean>> resourceCache;

	private Map<SecurityGlobalCacheId, Optional<Boolean>> globalCache;

	private Map<SecurityAnyContextCacheId, Optional<Boolean>> anyContextCache;

	private SecurityCacheRegistry() {
	}

	public static Optional<Boolean> checkResource(String userId, ResourceType resourceType, OperationType operationType, String resourceUuid) {
		return getInstance().checkResourceCache(userId, resourceType, operationType, resourceUuid);
	}

	public static Optional<Boolean> checkGlobal(String userId, ResourceType authObject, OperationType operationType) {
		return getInstance().checkGlobalCache(userId, authObject, operationType);
	}

	public static Optional<Boolean> checkAnyContext(String userId, ResourceType resourceType, OperationType operationType) {
		return getInstance().checkAnyContextCache(userId, resourceType, operationType);
	}

	public static boolean registerResource(String userId, ResourceType resourceType, OperationType operationType, String resourceUuid, Boolean result) {
		return getInstance().registerResourceCache(userId, resourceType, operationType, resourceUuid, result);
	}

	public static boolean registerGlobal(String userId, ResourceType authObject, OperationType operationType, Boolean result) {
		return getInstance().registerGlobalCache(userId, authObject, operationType, result);
	}

	public static boolean registerAnyContext(String userId, ResourceType resourceType, OperationType operationType, Boolean result) {
		return getInstance().registerAnyContextCache(userId, resourceType, operationType, result);
	}

	private Optional<Boolean> checkResourceCache(String userId, ResourceType resourceType, OperationType operationType, String resourceUuid) {
		if (StringUtils.isNotBlank(userId) && resourceType != null && operationType != null && StringUtils.isNotBlank(resourceUuid)) {
			Optional<Boolean> result = resourceCache.get(new SecurityResourceCacheId(userId, resourceType, operationType, resourceUuid));
			if (result != null) {
				return result;
			}
		}
		return Optional.empty();
	}

	private Optional<Boolean> checkGlobalCache(String userId, ResourceType authObject, OperationType operationType) {
		if (StringUtils.isNotBlank(userId) && authObject != null && operationType != null) {
			Optional<Boolean> result = globalCache.get(new SecurityGlobalCacheId(userId, authObject, operationType));
			if (result != null) {
				return result;
			}
		}
		return Optional.empty();
	}

	private Optional<Boolean> checkAnyContextCache(String userId, ResourceType resourceType, OperationType operationType) {
		if (StringUtils.isNotBlank(userId) && resourceType != null && operationType != null) {
			Optional<Boolean> result = anyContextCache.get(new SecurityAnyContextCacheId(userId, resourceType, operationType));
			if (result != null) {
				return result;
			}
		}
		return Optional.empty();
	}

	private boolean registerResourceCache(String userId, ResourceType resourceType, OperationType operationType, String resourceUuid, Boolean result) {
		if (StringUtils.isNotBlank(userId) && resourceType != null && operationType != null && StringUtils.isNotBlank(resourceUuid)) {
			resourceCache.put(new SecurityResourceCacheId(userId, resourceType, operationType, resourceUuid), Optional.of(result));
			return true;
		}
		return false;
	}

	private boolean registerGlobalCache(String userId, ResourceType authObject, OperationType operationType, Boolean result) {
		if (StringUtils.isNotBlank(userId) && authObject != null && operationType != null) {
			globalCache.put(new SecurityGlobalCacheId(userId, authObject, operationType), Optional.of(result));
			return true;
		}
		return false;
	}

	private boolean registerAnyContextCache(String userId, ResourceType resourceType, OperationType operationType, Boolean result) {
		if (StringUtils.isNotBlank(userId) && resourceType != null && operationType != null) {
			anyContextCache.put(new SecurityAnyContextCacheId(userId, resourceType, operationType), Optional.of(result));
			return true;
		}
		return false;
	}

	private static SecurityCacheRegistry getInstance() {
		SecurityCacheRegistry instance = threadLocal.get();
		if (instance == null) {
			instance = create();
		}
		return instance;
	}

	private static SecurityCacheRegistry create() {
		SecurityCacheRegistry instance = new SecurityCacheRegistry();
		instance.anyContextCache = new HashMap<>();
		instance.resourceCache = new HashMap<>();
		instance.globalCache = new HashMap<>();
		threadLocal.set(instance);
		return instance;
	}

	public static void close() {
		SecurityCacheRegistry instance = threadLocal.get();
		if (instance != null) {
			instance.doClose();
		}
	}

	private void doClose() {
		if (resourceCache != null) {
			resourceCache.clear();
			resourceCache = null;
		}
		if (globalCache != null) {
			globalCache.clear();
			globalCache = null;
		}
		if (anyContextCache != null) {
			anyContextCache.clear();
			anyContextCache = null;
		}
		threadLocal.remove();
	}

	private static class SecurityResourceCacheId {

		private final String userId;

		private final ResourceType resourceType;

		private final OperationType operationType;

		private final String resourceUuid;

		public SecurityResourceCacheId(String userId, ResourceType resourceType, OperationType operationType, String resourceUuid) {
			this.userId = userId;
			this.resourceType = resourceType;
			this.operationType = operationType;
			this.resourceUuid = resourceUuid;
		}

		@Override
		public int hashCode() {
			return 31 + Objects.hash(userId, resourceType, operationType, resourceUuid);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof SecurityResourceCacheId)) {
				return false;
			}
			SecurityResourceCacheId other = (SecurityResourceCacheId) obj;
			return userId.equals(other.userId) && resourceType.equals(other.resourceType)
					&& operationType.equals(other.operationType)
					&& resourceUuid.equals(other.resourceUuid);
		}

	}

	private static class SecurityGlobalCacheId {

		private final String userId;

		private final ResourceType authObject;

		private final OperationType operationType;

		public SecurityGlobalCacheId(String userId, ResourceType authObject, OperationType operationType) {
			this.userId = userId;
			this.authObject = authObject;
			this.operationType = operationType;
		}

		@Override
		public int hashCode() {
			return 31 + Objects.hash(userId, authObject, operationType);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof SecurityGlobalCacheId)) {
				return false;
			}
			SecurityGlobalCacheId other = (SecurityGlobalCacheId) obj;
			return userId.equals(other.userId) && authObject.equals(other.authObject)
					&& operationType.equals(other.operationType);
		}

	}

	private static class SecurityAnyContextCacheId {

		private final String userId;

		private final ResourceType resourceType;

		private final OperationType operationType;

		public SecurityAnyContextCacheId(String userId, ResourceType resourceType, OperationType operationType) {
			this.userId = userId;
			this.resourceType = resourceType;
			this.operationType = operationType;
		}

		@Override
		public int hashCode() {
			return 31 + Objects.hash(userId, resourceType, operationType);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof SecurityAnyContextCacheId)) {
				return false;
			}
			SecurityAnyContextCacheId other = (SecurityAnyContextCacheId) obj;
			return userId.equals(other.userId) && resourceType.equals(other.resourceType)
					&& operationType.equals(other.operationType);
		}

	}

}
