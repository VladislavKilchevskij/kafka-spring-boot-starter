# kafka-spring-boot-starter
Custom Kafka starter for Spring Boot

For Maven:
1. Add repository
   ```xml
   <repositories>
        <repository>
            <id>kafka-spring-boot-starter-mvn-repo</id>
            <url>https://raw.github.com/VladislavKilchevskij/kafka-spring-boot-starter/mvn-repo/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
   ```
2. Add dependency
   ```xml
   <dependency>
        <groupId>com.aston.libraries.starters</groupId>
        <artifactId>kafka-spring-boot-starter</artifactId>
        <version>0.0.1-SNAPSHOT</version>
   </dependency>
   ```
