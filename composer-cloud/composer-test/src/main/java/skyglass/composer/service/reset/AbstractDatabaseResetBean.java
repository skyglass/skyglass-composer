package skyglass.composer.service.reset;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import skyglass.composer.exceptions.ClientException;

/**
 *
 * @author skyglass
 */
public abstract class AbstractDatabaseResetBean {
	private static final Logger log = LoggerFactory.getLogger(AbstractDatabaseResetBean.class);

	private static final String[] RESET_TABLE_NAMES = { "USERCONTEXTPERMISSION", "USERCONTEXTROLE", "ROLEPERMISSION", "ROLE" };

	private static final String[] RESET_H2_TABLE_NAMES = { "ROLE_HIERARCHYVIEW" };

	private static final String[] KEEP_USER_UUIDS = new String[] { "02655648-7238-48e5-a36e-45025559b219" };

	private static final String[] KEEP_BUSINESSOWNER_UUIDS = new String[] { "6018d2e1-b94b-424a-840c-9cbae9074f4e" };

	private static final TableContainer[] PARTIALLY_RESET_TABLES = new TableContainer[] {
			new TableContainer("USER_GLOBALROLES", "USER_UUID", true, KEEP_USER_UUIDS),
			new TableContainer("\"USER\"", KEEP_USER_UUIDS),
			new TableContainer("BUSINESSOWNER", KEEP_BUSINESSOWNER_UUIDS)
	};

	private static final TableContainer[] PARTIALLY_RESET_H2_TABLES = new TableContainer[] {

	};

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	/**
	 * Checks if direct database modification is allowed for the currently connected database.
	 * If not allowed an {@link UnsupportedOperationException} will be thrown.
	 *
	 * @return {@code true} if a reset should be executed, otherwise {@code false},
	 *         if no reset can be done an exception will be thrown
	 *
	 * @throws IllegalStateException if the database connection is not established at the moment
	 * @throws UnsupportedOperationException if the database is not allowed to be modified
	 */
	protected abstract boolean checkDatabase() throws IllegalStateException, UnsupportedOperationException;

	protected String[] getAdditionalResetTableNames() {
		return new String[0];
	}

	protected TableContainer[] getAdditionPartiallyResetTables() {
		return new TableContainer[0];
	}

	protected boolean checkDataIntegrity() {
		return true;
	}

	/**
	 * Returns surroundings for column or table names if needed.
	 *
	 * @return surroundings for column or table names if needed
	 */
	protected abstract String getQueryNamesSurroundings();

	/**
	 * Resets the database data of the currently connected database.
	 *
	 * @return {@code true} if the database was reset, {@code false} if it wasn't necessary
	 *
	 * @throws ClientException if there is an issue with the data
	 * @throws DataAccessException if there is an issue with the SQL scripts
	 */
	protected boolean resetDatabaseData() throws ClientException, DataAccessException {
		return resetH2DatabaseData(PARTIALLY_RESET_H2_TABLES, RESET_H2_TABLE_NAMES)
				&& resetDatabaseData(PARTIALLY_RESET_TABLES, RESET_TABLE_NAMES)
				&& resetDatabaseData(getAdditionPartiallyResetTables(), getAdditionalResetTableNames());
	}

	protected boolean resetH2DatabaseData(TableContainer[] partiallyResetTables, String[] resetTableNames) {
		if (checkH2Database()) {
			return resetDatabaseData(partiallyResetTables, resetTableNames);
		}
		return true;
	}

	protected boolean resetDatabaseData(TableContainer[] partiallyResetTables, String[] resetTableNames) throws ClientException, DataAccessException {

		String columnNameSurroundings = getQueryNamesSurroundings();

		for (String tableName : resetTableNames) {
			jdbcTemplate.execute("DELETE FROM " + columnNameSurroundings + tableName + columnNameSurroundings);

			log.info("Truncated " + tableName + " table completely");
		}

		for (TableContainer tableContainer : partiallyResetTables) {
			int count = -1;

			String keepValuesCountQuery = tableContainer.toKeepValuesCountQuery(columnNameSurroundings);
			if (!StringUtils.isBlank(keepValuesCountQuery)) {
				count = jdbcTemplate.queryForObject(keepValuesCountQuery, Integer.class);
			}

			if (!checkDataIntegrity() || count == -1 || count == tableContainer.getAmountOfKeepValues()) {
				jdbcTemplate.execute(tableContainer.toDeleteQuery(columnNameSurroundings));

				log.info("Cleaned up " + tableContainer.getTableName() + " table and kept " + count + " value(s)");
			} else if ("UUID".equals(tableContainer.getColumnName())) {
				throw new ClientException(HttpStatus.CONFLICT,
						"The amount of non-removed values (" + count + ") is not matching the expected data in the "
								+ tableContainer.getTableName() + " table (" + tableContainer.getAmountOfKeepValues()
								+ ")! Maybe wrong UUIDs, wrong table or wrong column to check?!");
			}
		}

		return true;
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
