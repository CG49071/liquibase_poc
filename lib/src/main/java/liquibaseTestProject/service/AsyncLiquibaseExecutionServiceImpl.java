package liquibaseTestProject.service;

import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import liquibaseTestProject.AbstractDBUtilitiesApplication;
import liquibaseTestProject.DBUtilitiesConstants;


@Service
public class AsyncLiquibaseExecutionServiceImpl extends AbstractDBUtilitiesApplication implements AsyncLiquibaseExecutionService {
	
	@Override
	@Async("threadPoolTaskExecutorLiquibase")
	public CompletableFuture<String> asyncTenantUpgrade(String tenantSchemaName,DataSource tenantSchemaDataSource, String appDBTenantSchemaMasterChangeLogPath, String transactionID) {
		tagNumber = transactionID;
		applyActionOnDatabase(appDBTenantSchemaMasterChangeLogPath, DBUtilitiesConstants.ACTION_UPDATE, tenantSchemaDataSource, tenantSchemaName);
		return CompletableFuture.completedFuture(tenantSchemaName);
	}
	
	
	

}