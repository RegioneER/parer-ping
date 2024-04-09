---
title: "Configurazione Sacer Ping"
---

# Configurazione Jboss EAP 7.3

## Versioni 

| Vers. doc | Vers. Ping  | Modifiche  |
| -------- | ---------- | ---------- |
| 1.0.0 | 4.8.3 | Versione iniziale del documento  |

## Datasource XA

### Console web

`Configuration > Connector > datasources`

Scegliere **XA DATASOURCES** e premere 

`Add`

Si apre un wizard in 3 passaggi
1. Aggiungere gli attributi del datasource: Nome=**PingPool** e JNDI=**java:/jboss/datasources/PingDs**
2. Selezionare il driver **ojdbc8** (predisposto durante la configurazione generale di Jboss) e impostare **oracle.jdbc.xa.client.OracleXADataSource** come XA Data Source Class;
3. Impostare gli attributi della connessione, ad esempio *URL* 

#### JBoss CLI

```bash
xa-data-source add --name=PingPool --jndi-name=java:jboss/datasources/PingDs --xa-datasource-properties={"URL"=>"jdbc:oracle:thin:@parer-vora-b03.ente.regione.emr.it:1521/PARER19S.ente.regione.emr.it"} --user-name=SACER_PING --password=<password> --driver-name=ojdbc8 --max-pool-size=64 --spy=true --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.oracle.OracleExceptionSorter --stale-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.oracle.OracleStaleConnectionChecker --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.oracle.OracleValidConnectionChecker --statistics-enabled=true --use-ccm=true --use-fast-fail=true --validate-on-match=true --flush-strategy=FailingConnectionOnly --background-validation=false --min-pool-size=8 --enabled=true --allow-multiple-users=false --connectable=false  --set-tx-query-timeout=false --share-prepared-statements=false --track-statements=NOWARN
```

### Transaction service 

Lo schema dell'applicazione ha bisogno delle seguenti grant su Oracle.

```sql
GRANT SELECT ON sys.dba_pending_transactions TO SACER_PING;
GRANT SELECT ON sys.pending_trans$ TO SACER_PING;
GRANT SELECT ON sys.dba_2pc_pending TO SACER_PING;
GRANT EXECUTE ON sys.dbms_xa TO SACER_PING;
```
La procedura è descritta nella documentazione standard di JBoss EAP 7.3

https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.3/html/configuration_guide/datasource_management#vendor_specific_xa_recovery

## Configurazione Servizio JMS

Per la configurazione del subsystem si rimanda alla documentazione generale di JBoss EAP 7.3 del ParER.  
Una volta fatto è necessario impostare le risorse JMS.

### Configurazione Risorse JMS e Nomi JNDI

#### Configurazione tramite interfaccia web

`Configuration > Messaging > Destinations` 

Andare in `View` sul **default**  quindi 

`Queues/Topics > Queue`

Cliccare su 

`Add` 

Aggiungere Name=**ProducerCodaVersQueue** e JNDI=**java:/jms/queue/ProducerCodaVersQueue**

#### Configurazione tramite CLI

```bash
jms-queue add --queue-address=ProducerCodaVersQueue --entries=[java:/jms/queue/ProducerCodaVersQueue]
```

### Bean pool per gli MDB

#### Configurazione tramite interfaccia web

`Configuration > Container > EJB 3 > BEAN POOLS`

Aggiungere i seguenti Bean Pools  

Name | Max Pool Size | 
--- | --- |
coda1-pool | 3
coda2-pool | 2
coda3-pool | 1
coda-verifica-hash-pool | 1

#### Configurazione tramite CLI

Sostituire la keyword *{my-profile}* con la keyword adeguata.

```bash
/subsystem=ejb3/strict-max-bean-instance-pool=coda-verifica-hash-pool:add(max-pool-size=1)

/subsystem=ejb3/strict-max-bean-instance-pool=coda1-pool:add(max-pool-size=3)

/subsystem=ejb3/strict-max-bean-instance-pool=coda2-pool:add(max-pool-size=2)

/subsystem=ejb3/strict-max-bean-instance-pool=coda3-pool:add(max-pool-size=1)
```

## Key Store 

È necessario mettere il keystore in formato JKS in una cartella accessibile all'IDP e poi configurare la system properties sacerping-jks-path con il path al file.

## System properties

### Console web 

`Configuration > System properties`

impostare le seguenti properties

Chiave | Valore di esempio | Descrizione
--- | --- | ---
sacerping-key-manager-pass | <password_jks_sacerping> | Chiave del Java Key Store utilizzato per ottenere la chiave privata del circolo di fiducia dell’IDP.
sacerping-timeout-metadata | 10000 | Timeout in secondi per la ricezione dei metadati dall’IDP.
sacerping-temp-file | /var/tmp/tmp-sacerping-federation.xml | Percorso assoluto del file xml che rappresenta l’applicazione all’interno del circolo di fiducia.
sacerping-sp-identity-id | https://parer-snap.regione.emilia-romagna.it/sacerping | Identità dell’applicazione all’interno del circolo di fiducia.
sacerping-refresh-check-interval | 600000 | Intervallo di tempo in secondi utilizzato per ricontattare l’IDP per eventuali variazioni sulla configurazione del circolo di fiducia.
sacerping-jks-path | /opt/jboss-eap/certs/sacerping.jks | Percorso assoluto del Java Key Store dell’applicazione.
sacerping-store-key-name | sacerping | Alias del certificato dell’applicazione all’interno del Java Key Store.
su.aws.accessKeyId | <accessKeyId_object_storage_su> | Access Key id delle credenziali S3 per l’accesso all’object storage per il servizio degli strumenti urbanistici.
su.aws.secretKey | <secretKey_object_storage_su> | Secret Key delle credenziali S3 per l’accesso all’object storage per il servizio degli strumenti urbanistici.
xf-object.aws.accessKeyId | <accessKeyId_object_storage_pigObject> | Access Key id delle credenziali S3 per l’accesso all’object storage per i pig Object
xf-object.aws.secretKey | <secretKey_object_storage_pigObject> | Secret Key delle credenziali S3 per l’accesso all’object storage per i pig Object

### jboss cli

```bash
/system-property=sacerping-key-manager-pass:add(value="<password_jks_sacerping>")
/system-property=sacerping-timeout-metadata:add(value="10000")
/system-property=sacerping-temp-file:add(value="/var/tmp/tmp-sacerping-federation.xml")
/system-property=sacerping-sp-identity-id:add(value="https://parer-snap.regione.emilia-romagna.it/sacerping")
/system-property=sacerping-refresh-check-interval:add(value="600000")
/system-property=sacerping-jks-path:add(value="/opt/jboss-eap/certs/sacerping.jks")
/system-property=sacerping-store-key-name:add(value="sacerping")
/system-property=su.aws.accessKeyId:add(value="<accessKeyId_object_storage_su>")
/system-property=su.aws.secretKey:add(value="<secretKey_object_storage_su>")
/system-property=xf-object.aws.accessKeyId:add(value="<accessKeyId_object_storage_pigObject>")
/system-property=xf-object.aws.secretKey:add(value="<secretKey_object_storage_pigObject>")
/system-property=sisma.aws.secretKey:add(value="<secretKey_object_storage_sisma>")
/system-property=sisma.aws.accessKeyId:add(value="<accessKeyId_object_storage_sisma>")
```

## Logging profile

### JDBC custom handler
Assicurarsi di aver installato il modulo ApplicationLogCustomHandler (Vedi documentazione di configurazione di Jboss EAP 7.3).

Configurare un custom handler nel subsystem **jboss:domain:logging:1.5**.

```xml
<subsystem xmlns="urn:jboss:domain:logging:1.5">
    <!-- ... --> 
    <custom-handler name="sacerping_jdbc_handler" class="it.eng.tools.jboss.module.logger.ApplicationLogCustomHandler" module="it.eng.tools.jboss.module.logger">
        <level name="INFO"/>
        <formatter>
            <named-formatter name="PATTERN"/>
        </formatter>
        <properties>
            <property name="fileName" value="sacerping_jdbc.log"/>
            <property name="deployment" value="sacerping"/>
        </properties>
    </custom-handler>
    <!-- ... -->
</subsystem>
```
I comandi CLI 

```bash 
/subsystem=logging/custom-handler=sacerping_jdbc_handler:add(class=it.eng.tools.jboss.module.logger.ApplicationLogCustomHandler,module=it.eng.tools.jboss.module.logger,level=INFO)

/subsystem=logging/custom-handler=sacerping_jdbc_handler:write-attribute(name=named-formatter,value=PATTERN)

/subsystem=logging/custom-handler=sacerping_jdbc_handler:write-attribute(name=properties,value={fileName=>"sacerping_jdbc.log", deployment=>"sacerping"})
```

Associare l'handler ai logger **jboss.jdbc.spy** e **org.hibernate**, sempre nel subsystem **jboss:domain:logging:1.5**. 


```xml
<subsystem xmlns="urn:jboss:domain:logging:1.5">
    <!-- ... -->
    <logger category="jboss.jdbc.spy" use-parent-handlers="false">
        <level name="DEBUG"/>
        <filter-spec value="match(&quot;Statement|prepareStatement&quot;)"/>
        <handlers>
            <handler name="sacerping_jdbc_handler"/>
        </handlers>
    </logger>
    <logger category="org.hibernate" use-parent-handlers="false">
        <level name="WARNING"/>
        <handlers>
            <handler name="sacerping_jdbc_handler"/>
        </handlers>
    </logger>
    <!-- ... -->
</subsystem>
```

I comandi CLI

```bash
/subsystem=logging/logger=org.hibernate:add-handler(name=sacerping_jdbc_handler)

/subsystem=logging/logger=jboss.jdbc.spy:add-handler(name=sacerping_jdbc_handler)
```
### Profilo di Ping

#### JBoss CLI 

```bash
/subsystem=logging/logging-profile=SACERPING:add()
/subsystem=logging/logging-profile=SACERPING/periodic-rotating-file-handler=sacerping_handler:add(level=INFO,formatter="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n",file={path="sacerping.log",relative-to="jboss.server.log.dir"},suffix=".yyyy-MM-dd",append=true)
/subsystem=logging/logging-profile=SACERPING/size-rotating-file-handler=sacerping_tx_connection_handler:add(level=DEBUG,formatter="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n",file={path="sacerping_conn_handler.log",relative-to="jboss.server.log.dir"},append=true,max-backup-index=1,rotate-size="256m")
/subsystem=logging/logging-profile=SACERPING/root-logger=ROOT:add(level=INFO,handlers=[sacerping_handler])
/subsystem=logging/logging-profile=SACERPING/logger=org.springframework:add(level=ERROR,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERPING/logger=org.exolab.castor.xml.NamespacesStack:add(level=OFF,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERPING/logger=org.exolab.castor.xml.EndElementProcessor:add(level=ERROR,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERPING/logger=it.eng.sacerasi:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERPING/logger=org.opensaml:add(level=ERROR,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERPING/logger=org.hibernate:add(level=ERROR,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERPING/logger=jboss.jdbc.spy:add(level=ERROR,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERPING/logger=org.jboss.jca.core.connectionmanager.listener.TxConnectionListener:add(level=DEBUG,handlers=[sacerping_tx_connection_handler],use-parent-handlers=false)
```

```xml
<logging-profiles>
    <!-- ... -->
    <logging-profile name="SACERPING">
        <periodic-rotating-file-handler name="sacerping_handler">
            <level name="DEBUG"/>
            <formatter>
                <pattern-formatter pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
            </formatter>
            <file relative-to="jboss.server.log.dir" path="sacerping.log"/>
            <suffix value=".yyyy-MM-dd"/>
            <append value="true"/>
        </periodic-rotating-file-handler>
        <periodic-size-rotating-file-handler name="sacerping_tx_connection_handler" autoflush="true">
            <level name="DEBUG"/>
            <formatter>
                <pattern-formatter pattern="%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
            </formatter>
            <file relative-to="jboss.server.log.dir" path="sacerping_conn_handler.log"/>
            <append value="true"/>
            <max-backup-index value="1">
            <rotate-size value="256m"/>
        </periodic-size-rotating-file-handler>
        <logger category="org.springframework" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="org.exolab.castor.xml.NamespacesStack" use-parent-handlers="true">
            <level name="OFF"/>
        </logger>
        <logger category="org.exolab.castor.xml.EndElementProcessor" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="it.eng.sacerasi" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="org.opensaml" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="org.hibernate" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="jboss.jdbc.spy" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="org.jboss.jca.core.connectionmanager.listener.TxConnectionListener" use-parent-handlers="false">
            <level name="DEBUG"/>
            <handlers>
                <handler name="sacerping_tx_connection_handler"/>
            </handlers>
        </logger>
        <root-logger>
            <level name="INFO"/>
            <handlers>
                <handler name="sacerping_handler"/>
            </handlers>
        </root-logger>
    </logging-profile>
    <!-- ... -->
</logging-profiles>
```

