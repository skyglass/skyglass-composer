package skyglass.composer.service.reset;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author skyglass
 */
public class TableContainer {
	private String tableName;

	private String columnName;

	private String[] keepValues;

	private boolean optionalValues;

	public TableContainer(String tableName, String[]... keepValues) {
		this(tableName, false, keepValues);
	}

	public TableContainer(String tableName, boolean optionalValues, String[]... keepValues) {
		this(tableName, "UUID", optionalValues, keepValues);
	}

	public TableContainer(String tableName, String columnName, String[]... keepValues) {
		this(tableName, columnName, false, keepValues);
	}

	public TableContainer(String tableName, String columnName, boolean optionalValues, String[]... keepValues) {
		this.tableName = tableName;
		this.columnName = columnName;
		this.optionalValues = optionalValues;

		this.keepValues = new String[0];
		for (String[] kv : keepValues) {
			this.keepValues = ArrayUtils.addAll(this.keepValues, kv);
		}
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setKeepValues(String... keepValues) {
		this.keepValues = keepValues;
	}

	public void setOptionalValues(boolean optionalValues) {
		this.optionalValues = optionalValues;
	}

	public int getAmountOfKeepValues() {
		return keepValues == null ? 0 : keepValues.length;
	}

	private static String toCommaSeparatedString(String... values) {
		if (values == null) {
			return "";
		}

		boolean first = true;
		StringBuilder builder = new StringBuilder();
		for (String value : values) {
			if (StringUtils.isBlank(value)) {
				continue;
			}

			if (!first) {
				builder.append(", ");
			}

			first = false;

			builder.append("'");
			builder.append(value);
			builder.append("'");
		}

		return builder.toString();
	}

	public String toKeepValuesCountQuery(String columnNameSurrounding) {
		if (!optionalValues) {
			String commaSeparatedValues = toCommaSeparatedString(keepValues);
			if (!StringUtils.isBlank(commaSeparatedValues)) {
				return "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnNameSurrounding + columnName + columnNameSurrounding + " IN (" + commaSeparatedValues + ")";
			}
		}

		return null;
	}

	public String toDeleteQuery(String columnNameSurrounding) {
		String commaSeparatedValues = toCommaSeparatedString(keepValues);
		if (StringUtils.isBlank(commaSeparatedValues)) {
			return "TRUNCATE TABLE \"" + tableName + "\"";
		} else {
			return "DELETE FROM " + tableName + " WHERE " + columnNameSurrounding + columnName + columnNameSurrounding + " NOT IN (" + commaSeparatedValues + ")";
		}
	}
}
