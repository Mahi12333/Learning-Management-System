<!--<?xml version="1.0" encoding="UTF-8"?>-->
<!--<configuration>-->

<!--    <springProperty scope="context" name="SPRING_APP_NAME" source="spring.application.name"/>-->

<!--    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">-->
<!--        <encoder>-->
<!--            <pattern>-->
<!--                %d{dd-MM-yyyy HH:mm:ss.SSS} [%thread] [%5p] [${SPRING_APP_NAME}]-->
<!--                [session-id=%X{session-id}]-->
<!--                [traceId=%X{X-B3-TraceId}]-->
<!--                [%c{36}::%L::%M] &#45;&#45; %m%n-->
<!--            </pattern>-->
<!--        </encoder>-->
<!--    </appender>-->

<!--    &lt;!&ndash; Direct logs to Logstash in JSON &ndash;&gt;-->
<!--    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">-->
<!--        <destination>logstash:5000</destination>-->
<!--        <encoder class="net.logstash.logback.encoder.LogstashEncoder" />-->
<!--    </appender>-->

<!--    &lt;!&ndash; Dev profile &ndash;&gt;-->
<!--    <springProfile name="dev">-->
<!--        <root level="debug">-->
<!--            <appender-ref ref="STDOUT" />-->
<!--        </root>-->
<!--    </springProfile>-->

<!--    &lt;!&ndash; Prod profile &ndash;&gt;-->
<!--    <springProfile name="prod">-->
<!--        <root level="info">-->
<!--            <appender-ref ref="STDOUT" />-->
<!--            <appender-ref ref="LOGSTASH" />-->
<!--        </root>-->
<!--    </springProfile>-->

<!--</configuration>-->







-----------------
logback-spring.xml

Old

<!--<?xml version="1.0" encoding="UTF-8"?>-->
<!--<configuration>-->
<!--    &lt;!&ndash;    <include resource="org/springframework/boot/logging/logback/base.xml"/>&ndash;&gt;-->
<!--    <springProperty scope="context" name="springAppName" source="spring.application.name"/>-->

<!--    &lt;!&ndash; Logstash Appender &ndash;&gt;-->
<!--    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">-->
<!--        &lt;!&ndash;     <destination>${LOGSTASH_HOST}:${LOGSTASH_PORT}</destination> &ndash;&gt;-->
<!--        <destination>localhost:5000</destination>-->
<!--        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>-->
<!--    </appender>-->

<!--    &lt;!&ndash; Console Appender &ndash;&gt;-->
<!--    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">-->
<!--        <encoder>-->
<!--            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) -&#45;&#45; [${springAppName},%X{traceId:-},%X{spanId:-}] %cyan(%logger{15}) : %msg%n</pattern>-->
<!--        </encoder>-->
<!--    </appender>-->

<!--    &lt;!&ndash; Root Logger &ndash;&gt;-->
<!--    <root level="INFO">-->
<!--        &lt;!&ndash;        comment for developent&ndash;&gt;-->
<!--        &lt;!&ndash;        <appender-ref ref="LOGSTASH"/>&ndash;&gt;-->
<!--        <appender-ref ref="CONSOLE"/>-->
<!--    </root>-->

<!--    &lt;!&ndash; Custom Log Levels &ndash;&gt;-->
<!--    <logger name="org.springframework" level="INFO"/>-->
<!--    <logger name="com.maven.Rapido" level="TRACE"/>-->
<!--</configuration>-->



--------------------------------
* Permissions checking----
@OwnerCheck(entity="Course", idParam="courseId")
@ProfileCompleteCheck(entity="User")


----------------------------------------
Logs-----
1. Logging (Logback + Logstash)
Spring Boot → Logback → Logstash → Elasticsearch → Kibana
UI: Ye logs Kibana UI mein dikhenge.

Default Port: http://localhost:5601 (Kibana ka port).

Kibana se aap search kar sakte ho @log_name, traceId, sessionId, etc.



2. Audit Logs (Elasticsearch + Kibana)



Matlab: Application logs → sab dikhenge Kibana mein

Audit logs → sirf @Auditable waale methods ke events dikhenge.

Logging UI (Logback + Logstash) → Kibana (http://localhost:5601)

Audit Logs (Elasticsearch + Kibana) → Bhi Kibana par (http://localhost:5601), lekin alag index (audit-logs-*)

@Auditable → Sirf specific business actions capture karega (audit ke liye).

-------------------------------------------
Matlab:

Aapki main API requests chalengi yahaan →

http://localhost:5002/...


Aur Actuator endpoints (health, metrics, prometheus etc.) chalenge yahaan →

http://localhost:8080/actuator/health
http://localhost:8080/actuator/prometheus
-----------------------------------------------
All Logs Endpoint ----
Spring Boot APIs → http://localhost:5002/...
(Ye aapke application ka main API port hai)

Spring Boot Metrics (Actuator + Prometheus endpoint) → http://localhost:8080/actuator/prometheus
(Ye alag management server port hai jo aapne management.server.port=8080 diya hai)

Prometheus → http://localhost:9090

Grafana → http://localhost:3000 (default login: admin/admin)

Elasticsearch → http://localhost:9200

Kibana → http://localhost:5601

Logstash TCP/HTTP Input (API nahi hota, data ingestion hota hai) → http://localhost:5000 (yaha aap app logs bhejte ho, lekin ispe normally UI nahi hota — bas pipeline input hota hai).

🔹 Important Notes:

Logstash ke liye http://localhost:5000 pe browser se kuch open nahi hoga ❌, kyunki wo ek UI nahi hai. Ye ek input port hai jaha Spring Boot (ya beats/filebeat) logs bhejta hai.

Kibana hi wo UI hai jaha aap Elasticsearch/Logstash se aayi hui logs ko visualize karte ho.

Prometheus ka apna UI hai (basic queries ke liye), lekin visualization ke liye Grafana use karte hain.
----------------------------------------------------------------------------------
