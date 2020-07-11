package skyglass.composer.local.bean;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import skyglass.composer.exceptions.ClientException;
import skyglass.composer.service.reset.AbstractDatabaseResetBean;
import skyglass.composer.service.reset.TableContainer;

/**
 *
 * @author skyglass
 */
@Service
public class LocalDatabaseResetBean extends AbstractDatabaseResetBean {

	private static final TableContainer[] PARTIALLY_RESET_TABLES = new TableContainer[] {

	};

	@Override
	protected TableContainer[] getAdditionPartiallyResetTables() {
		return PARTIALLY_RESET_TABLES;
	}

	@Override
	protected boolean checkDatabase() throws IllegalStateException, UnsupportedOperationException {
		try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			return StringUtils.containsIgnoreCase(metaData.getDriverName(), "postgre");
		} catch (SQLException ex) {
			throw new IllegalStateException("Could not establish database connection", ex);
		}
	}

	@Override
	protected boolean checkDataIntegrity() {
		return false;
	}

	@Override
	protected String getQueryNamesSurroundings() {
		return "";
	}

	public boolean resetDatabase() throws ClientException, DataAccessException {
		return resetDatabaseData();
	}
}
