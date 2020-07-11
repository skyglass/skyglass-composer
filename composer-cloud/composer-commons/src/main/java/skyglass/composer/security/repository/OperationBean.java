package skyglass.composer.security.repository;

import java.util.Map;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.bean.AEntityBean;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.security.domain.Operation;
import skyglass.composer.security.domain.OperationType;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class OperationBean extends AEntityBean<Operation> {

	public Operation findByName(OperationType name) {
		TypedQuery<Operation> query = entityBeanUtil.createQuery(
				"SELECT op FROM Operation op WHERE op.name = :name",
				Operation.class);
		query.setParameter("name", name);
		return EntityUtil.getSingleResultSafely(query);
	}

	public Operation getOperation(OperationType operationType, Map<OperationType, Operation> operationMap) {
		Operation operation = operationMap.get(operationType);
		if (operation == null) {
			operation = findByName(operationType);
			operationMap.put(operationType, operation);
		}
		return operation;
	}

}
