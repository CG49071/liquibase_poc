package liquibaseTestProject;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import liquibaseTestProject.service.AsyncLiquibaseExecutionService;


@Configuration
public class DatabaseConfig extends AbstractDBUtilitiesApplication {
	
	private DataSource dataSource;
	private String transactionID;
	private String appDBTenantSchemaMasterChangeLogPath = "/tenant-master-changelog.xml";

	
	@Autowired
	AsyncLiquibaseExecutionService asyncLiquibaseExecutionService;

	@Bean(name = "createDataSource")
    public void createDataSource() {	
       BasicDataSource dataSource = new BasicDataSource();
       dataSource.setUrl("jdbc:postgresql://postgres:5432/liquibaseTest?stringtype=unspecified");
	   dataSource.setUsername("liquibaseUser");
	   dataSource.setPassword("liquibasePassword");
	   dataSource.setDriverClassName("org.postgresql.Driver");
	   this.dataSource =  dataSource;
	}
    
    @Bean(name = "tenantDBUpgrade")
	@DependsOn("createDataSource")
	public void tenantDBUpgrade() {
		List<String> tenantSchemaList = findSchemaList(dataSource);
		if (!tenantSchemaList.isEmpty()) {
			tenantSchemaUpgrade(tenantSchemaList);
		}
	}
    
	private List<String> findSchemaList(DataSource dataSource) {
		LinkedList<String> tenantList = new LinkedList<>();
		String query = "SELECT schema_name FROM schema_list;";
		try (Statement stmt = dataSource.getConnection().createStatement();
				ResultSet rs = stmt
						.executeQuery(query);) {
			while (rs.next()) {
				String schemaName = rs.getString("schema_name");
				tenantList.add(schemaName);
			}
			
		} catch (Exception tenantListException) {
			System.out.println(tenantListException.getMessage());
		}
		return tenantList;
	}
	
	
	private void tenantSchemaUpgrade(List<String> tenantSchemaList)  {
		List<String> tenantStatusList = new ArrayList<>();
		this.transactionID = UUID.randomUUID().toString();
		try {
			@SuppressWarnings("unchecked")
			List<CompletableFuture<String>> futureList =  (List<CompletableFuture<String>>) tenantSchemaList.stream()
			.map(tenantSchemaName -> asyncLiquibaseExecutionService.asyncTenantUpgrade(tenantSchemaName,dataSource,appDBTenantSchemaMasterChangeLogPath,transactionID)).collect(Collectors.toList());
			CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
			futureList.stream().forEach(future -> tenantStatusList.add(future.join()));
		} catch (Exception tenantDBUpgrade) {
			throw tenantDBUpgrade;
		} finally {
			closeDBConnection(dataSource);	
			tenantSchemaList.clear();
		}
	}


}