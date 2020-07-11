package skyglass.composer.security.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import skyglass.composer.security.domain.Operation;
import skyglass.composer.security.domain.OperationType;

public class OperationProvider {

	private Map<OperationType, Operation> map = new HashMap<>();

	private Function<OperationType, Operation> provider;

	public OperationProvider(Function<OperationType, Operation> provider) {
		this.provider = provider;
	}

	public Operation get(OperationType operationType) {
		Operation operation = map.get(operationType);
		if (operation == null) {
			operation = provider.apply(operationType);
			map.put(operationType, operation);
		}
		return operation;
	}

}
