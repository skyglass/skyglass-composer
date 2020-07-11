package skyglass.composer.security.service;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import skyglass.composer.exceptions.ClientException;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.repository.PermissionBean;

/**
 * Filter that checks if the user has permission to access the authorization
 * object.
 *
 * @author skyglass
 *
 */
public class SecureInterceptor implements HandlerInterceptor {
	@Autowired
	private PermissionBean permissionBean;

	@Autowired
	private SecurityCheckService securityCheckService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler instanceof HandlerMethod) {
			Map<ResourceType, OperationType> authObjectPermissionMap = Collections.emptyMap();

			User user = permissionBean.getUserFromCtx();
			if (user == null) {
				throw new ClientException(HttpStatus.FORBIDDEN,
						"Permission denied, because calling user could not be determined");
			} else {

				for (Map.Entry<ResourceType, OperationType> entry : authObjectPermissionMap.entrySet()) {
					ResourceType authObject = entry.getKey();
					OperationType opPermission = entry.getValue();
					if (opPermission == OperationType.Read && !securityCheckService.canReadForAnyContext(null, authObject)
							|| opPermission == OperationType.Write
									&& !securityCheckService.canWriteForAnyContext(null, authObject)) {
						throw new ClientException(HttpStatus.FORBIDDEN,
								"Permission denied for user " + user.getName() + " requesting " + opPermission.name()
										+ " permission on " + authObject.name());
					}
				}
			}

			return true;
		}

		// If handler is not a HandlerMethod we can allow it, because it doesn't
		// need to be protected, e.g. some ResourceHttpRequestHandler (which
		// could be loading of an image)
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// do nothing
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// do nothing
	}

}
