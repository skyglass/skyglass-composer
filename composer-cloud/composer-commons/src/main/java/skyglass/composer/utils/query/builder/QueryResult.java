package skyglass.composer.utils.query.builder;

import java.io.Serializable;
import java.util.List;

public class QueryResult<T> implements Serializable {

	private static final long serialVersionUID = -2045986229298564328L;

	protected List<T> result;

	protected int totalCount = -1;

	public QueryResult() {

	}

	public QueryResult(List<T> result, int totalCount) {
		this.result = result;
		this.totalCount = totalCount;
	}

	protected QueryResult(QueryResult<T> original) {
		setResult(original.getResult());
		setTotalCount(original.getTotalCount());
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
