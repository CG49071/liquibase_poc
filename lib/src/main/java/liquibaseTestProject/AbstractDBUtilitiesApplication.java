package liquibaseTestProject;

import java.sql.SQLException;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.ThreadLocalScopeManager;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

public abstract class AbstractDBUtilitiesApplication {
	
	protected String applicationLevel = DBUtilitiesConstants.DB_LEVEL;
	protected String tagNumber = null;
	protected String applicationContexts = DBUtilitiesConstants.DB_CONTEXTS;
	protected String transactionID = null;
	protected String defaultDBSchemaName = DBUtilitiesConstants.POSTGRESQL_DB_SCHEMA_NAME;
	private ClassLoaderResourceAccessor classPathResourceAccessor;

	
	
	private ResourceAccessor createResourceAccessor(final String changeLogPath) {
		Thread currentThread = Thread.currentThread();
		ClassLoader contextClassLoader = currentThread.getContextClassLoader();
		return this.classPathResourceAccessor = new ClassLoaderResourceAccessor(contextClassLoader);
	}
	
	protected void closeDBConnection(DataSource ds) {
		if (Objects.isNull(ds)) {
			return;
		}
		
		if (ds instanceof BasicDataSource) {
			BasicDataSource bds = (BasicDataSource) ds;
			try {
				bds.close();
			} catch (SQLException e) {
				e.getStackTrace();
			}
		}
	}
	protected void applyActionOnDatabase(String appDBTenantSchemaMasterChangeLogPath,String action, DataSource dataSource, String tenantSchemaName) {
		Liquibase liquibase = null;
		try {
			Scope.setScopeManager(new ThreadLocalScopeManager());
			ResourceAccessor resourceAccessor = createResourceAccessor(appDBTenantSchemaMasterChangeLogPath);
			Database database = createDatabase(dataSource, tenantSchemaName);
			Contexts context = new Contexts(applicationContexts);
			LabelExpression labelExpression = new LabelExpression(applicationLevel);
			liquibase = createLiquibase(appDBTenantSchemaMasterChangeLogPath, resourceAccessor, database);
			doActionOnLiquibase(appDBTenantSchemaMasterChangeLogPath, action,  liquibase,  context,  labelExpression);
		} catch (Exception exp) {
			System.out.println(exp.getMessage());
		} finally {
			closeDatabase(liquibase);
		}
	}
	
	
	private void doActionOnLiquibase(String masterChangeLog, String action, Liquibase liquibase, Contexts context, LabelExpression labelExpression) throws LiquibaseException {
		if (DBUtilitiesConstants.ACTION_UPDATE.equalsIgnoreCase(action)) {
			liquibase.tag(tagNumber);
			liquibase.update(context, labelExpression);

		}
	}


	private void closeDatabase(Liquibase liquibase) {
		Database database = null;
		if (liquibase != null) {
			database = liquibase.getDatabase();
		}
		if (database != null) {
			try {
				database.close();
			} catch (DatabaseException e) {
				e.getStackTrace();
			}
		}
	}


	
	@SuppressWarnings("resource")
	private Liquibase createLiquibase(String masterChangeLogpath, ResourceAccessor resourceAccessor, Database database){
		Liquibase liquibase = new Liquibase(masterChangeLogpath, resourceAccessor, database);
		return liquibase;
	}

	private Database createDatabase(DataSource dataSource, String schemaName) throws DatabaseException, SQLException {
		Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
		database = setLiquibaseDatabaseProperties(schemaName, database);
		return database;
	}


	private Database setLiquibaseDatabaseProperties(String schemaName, Database database) throws DatabaseException {
		database.setDatabaseChangeLogLockTableName(DBUtilitiesConstants.DATABASE_CHANGE_LOG_LOCK_TBL_NAME);
		database.setDatabaseChangeLogTableName(DBUtilitiesConstants.DATABASE_CHANGE_LOG_TBL_NAME);
		database.setDefaultSchemaName(schemaName.toLowerCase());
		return database;
	}
	
	

}
