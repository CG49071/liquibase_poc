This project recreate the issue mentioned in https://github.com/liquibase/liquibase/issues/5426
1. codeSetup - It uses SpringBoot 2.7.15, Liquiabse 4.25.0, Gradle 7.4, Java 11
   For creating war file
   1. Open CMD > Goto liquibase_poc
   2. Run command 'gradle war'
   3. liquibase-impl.war will be created in liquibase_poc\lib\build\libs
      
2. dockerSetup - This folder contains configuration for docker containers
   1. postgres container initialze multiple schema tenant structure
   2. tomcat container run the war created by #1.3. Place the war in tomcat folder
   3. Open CMD > Go to dockerSetup > Run command 'docker-compose up -d'
   4. Run command 'docker logs tomcat' to show the Liquibase error 

