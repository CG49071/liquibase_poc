package liquibaseTestProject.service;

import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

@Service
public interface AsyncLiquibaseExecutionService {
	
	public CompletableFuture<String> asyncTenantUpgrade(String tenantSchemaName,DataSource datasource, String appDBTenantSchemaMasterChangeLogPath, String transactionID);

}