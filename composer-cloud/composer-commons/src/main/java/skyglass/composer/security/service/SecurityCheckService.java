package skyglass.composer.security.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.repository.SecurityCheckBean;

/**
 *
 * @author skyglass
 */
@Service
@Transactional
public class SecurityCheckService {

	@Autowired
	private SecurityCheckBean securityCheckBean;

	public boolean canReadForAnyContext(String userId, ResourceType resourceType) {
		return securityCheckBean.canReadForAnyContext(userId, resourceType);
	}

	public boolean canWriteForAnyContext(String userId, ResourceType resourceType) {
		return securityCheckBean.canWriteForAnyContext(userId, resourceType);
	}

	public boolean canReadMvForAnyContext(String userId) {
		return securityCheckBean.canReadForAnyContext(userId, ResourceType.Mv);
	}

	public boolean canWriteMvForAnyContext(String userId) {
		return securityCheckBean.canWriteForAnyContext(userId, ResourceType.Mv);
	}

	public Map<ResourceType, OperationType> loadUserPermissions(String userId) {
		return securityCheckBean.loadUserPermissions(userId);
	}

}
