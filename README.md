# healthCheck
Spring boot service for http/smtp/ldap health checks

## Usage

Compile WAR:
```
mvn clean package
```

OR

Compile executable JAR:
```
mvn -P dev clean package
```

OR

Run Spring boot:
```
mvn spring-boot:run
```

WAR instructions:
* Copy target/healthcheck-x.x.x-SNAPSHOT.war to the tomcat's webapp directory
* Rename healthcheck-x.x.x-SNAPSHOT.war to health.war
* Modify WEB-INF/classes/application.properties OR create WEB-INF/classes/application-production.yml to override default parameters

JAR instructions: 
* Copy target/healthcheck-0.5.0-SNAPSHOT.jar to somewhere where you want to run it.
* Create directory "config" next to the jar-file
* Create config/application.properties. Override application defaults [application.properties](https://github.com/korteke/healthCheck/blob/master/src/main/resources/application.properties). At least resource addresses (http.address, smtp.address, ldap.address).
* Override "spring.profiles.active" variable is you want to define different run profiles [Spring Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html) and create corresponding properties / yml file.

Test application:   
curl http://server:8080/health

## Response

OK Response (HTTP 200):

```
{
  statusMessage: "Service is, UP",
  status: true
}
```   

Error response (HTTP 200):   
```
{
  statusMessage: "Service is, DOWN",
  status: false
}
```
