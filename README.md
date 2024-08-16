# PING (PreINGest) 

Fonte template redazione documento:  https://www.makeareadme.com/.


# Descrizione

Il modulo software PING (PreINGest) gestisce il processo di preacquisizione nel caso di versamento di Oggetti da trasformare in SIP. 


# Installazione

Requisiti minimi per installazione: 

- Sistema operativo : consigliato Linux server (in alternativa compatibilità con Windows server);
- Java versione 11 (OpenJDK / Oracle);
- JBoss 7 EAP;
- Oracle DB (versione consigliata 19c).

## Installazione JDK 

Consigliata adozione della OpenJDK alla versione 8, guida all'installazione https://openjdk.org/install/.

## Setup application server (Jboss 7)

Richiesta l'esecuzione delle seguenti guide secondo l'ordine riportato di seguito: 

1. guida per la configurazione **base** di [guida 1](src/docs/JBoss7_configurazione_generale.md);
2. guida con le configurazioni **specifiche** per il contesto applicativo **SIAM**  di [guida 2](src/docs/JBoss7_configurazione_siam.md).


### Deploy su JBoss 7

Di seguito le indicazioni per il rilascio su application server JBoss7: 

1. generazione dell'artifact attraverso tool maven, eseguire il seguente comando: 

   ```bash
   mvn package
   ```
   
2. viene generato l'artifact .ear all'interno del modulo preingest-ear/target (e.g. sacerping-4.22.0.ear)
3. deploy dell'ear generato allo step 1 su JBoss 7 (vedi configurazione [setup JBoss7](#setup-application-server-jboss-7))


## Predisposizione database

L'applicazione utilizza come DBMS di riferimento Oracle DB (https://www.oracle.com/it/database/) alla versione, consigliata, **19c**. Per l'installazione e la configurazione fare riferimento alle guide ufficiali.

Per la creazione del modello E-R consultare il seguente [README.md](https://github.com/RegioneER/parer-db-init/blob/master/README.md) (progetto di riferimento https://github.com/RegioneER/parer-db-init).

## Configurazione bucket S3 Object Storage

L'applicazione può essere integrata sulla base delle configurazioni impostate, vedi paragrafo Setup application server (Jboss 7), ad uno storage grid o object storage attraverso lo standard AWS S3.

### Configurazione dei bucket

Una possibile configurazione dei bucket proposta è la seguente, tra parentesi il nome del parametro da configurare su PING:

- xformer : Nome del bucket per archiviare i report delle trasformazioni richieste a Kettle Server (BUCKET_REPORT_XFORMER).
- sisma-trasformati : Nome del bucket per i progetti Sisma da trasformare (BUCKET_SISMA_TRASFORMATI).
- su-trasformati : Nome del bucket per gli Strumenti Urbanistici da trasformare (BUCKET_STRUMENTI_URBANISTICI_TRASFORMATI).
- sisma-verifica : Nome del bucket per la verifica dei progetti Sisma (BUCKET_VERIFICA_SISMA).
- su-verifica : Nome del bucket per la verifica degli Strumenti Urbanistici (BUCKET_VERIFICA_STRUMENTI_URBANISTICI).
- xf-object : Nome del bucket usato per versare oggetti se il tipo oggetto lo specifica (BUCKET_VERSAMENTO_OGGETTO).

# Utilizzo

La trasmissione dei pacchetti, solitamente compressi, avviene tramite avviene tramite interfaccia utente  o protocollo FTPS; l’FTP server provvede a memorizzare i file ricevuti sullo storage dedicato allo spazio FTP di input. 

Una volta ricevuti gli Oggetti, un job schedulato provvede alla loro elaborazione per la produzione dei SIP da versare. Un ulteriore job schedulato si occupa di effettuare il versamento a SacER, che avviene utilizzando un apposito servizio di versamento. Tale servizio accetta in chiamata due file XML, uno con l’Indice del SIP e un altro con le Informazioni sull’impacchettamento, relative alla posizione dei file del SIP memorizzati sullo spazio FTP di input. 

SacER utilizza le Informazioni sull’impacchettamento per recuperare i file dal file system di PING e depositarli nel proprio per le successive elaborazioni. 

Le successive elaborazioni vengono eseguite da PING direttamente, nel caso in cui la normalizzazione possa basarsi su regole precodificate (come p.e. nel caso delle immagini diagnostiche in formato Dicom), oppure utilizzando un motore ETL di esecuzione delle trasformazioni nel caso in cui si debbano applicare regole di trasformazioni specifiche dell’oggetto in questione. In questo caso le regole vengono definite durante la fase di avvio del servizio, tramite uno strumento visuale ed eventuali integrazioni di codice sviluppato ad hoc. 

PING traccia e memorizza nel proprio Data Base gli esiti dei versamenti a SacER e può essere interrogato da un operatore tramite interfaccia web o dal sistema versante tramite opportuno Web Service, per conoscere a quale punto del processo è giunto il SIP. 

PING mette inoltre a disposizione del Produttore un client di versamento di Oggetti da trasformare, sia on line, sia tramite l’utilizzo di un client FTP installato sulla postazione di lavoro dell’utente o su un server della rete del Produttore. 


### Amministrazione Versatori

<img src="src/docs/img/amm_versatori.png"> 

Il sistema gestisce l'anagrafe dei soggetti che possono versare gli oggetti, chiamati Versatori, questi possono essere di tipo: 

* PRODUTTORE
* SOGGETTO ATTUATORE 

I versatori di tipo PRODUTTORE sono collegati ad un ente convenzionato e hanno una struttura corrispondente in SACER. 

<img src="src/docs/img/dettaglio_versatore.png"> 

La configurazione del versatore prevede inoltre la possibilità di definire i tipi di oggetto gestiti. 

<img src="src/docs/img/dettaglio_tipo_ogg.png"> 


I Tipi oggetto possono essere di diverse categorie, distinguibili in due macrogruppi: 

* Oggetti che contengono dati grezzi che devono essere trasformati in SIP di UD per essere versati a SACER
  * DA TRASFORMARE – da trasformare con kettle-server
  * NO ZIP – da trasformare con PING 
* Oggetti che contengono uno o più SIP di UD da versare a SACER
  * ZIP NO XML SACER – da trasformare con PING (non utilizzato)
  * ZIP CON XML SACER – generato da trasformazione 

La configurazione di un tipo oggetto consente di definire alcune caratteristiche dello stesso, i controlli a cui deve essere sottoposto in fase di versamento, la priorità di tratttamento. 

<img src="src/docs/img/dettaglio_tipo_ogg_2.png"> 


### Versamenti

Il sistema offre delle interfacce utente per la gestione del versamento degli oggetti. 

<img src="src/docs/img/amm_versamenti.png"> 

È possibile scegliere il tipo oggetto da versare e indicare il path FTP nel quale si trova o eventualmente caricare direttamente il file ZIP dell'oggetto. 

<img src="src/docs/img/versamento_ogg_trasform.png"> 

Il sistema offre anche delle funzioni di ricerca degli oggetti versati 

<img src="src/docs/img/ricerca_ogg.png"> 

<img src="src/docs/img/dettaglio_ogg.png"> 

### Monitoraggio

<img src="src/docs/img/monitoraggio.png"> 

La sezione monitoraggio consente all'utente di monitorare l'andamento del processo di versamento in tutte le sue fasi. 

<img src="src/docs/img/riepilogo_versamenti.png"> 

### Gestione trasformazioni

<img src="src/docs/img/amm_trasformazioni.png"> 

Contiene l'anagrafe delle trasformazioni gestite dal sistema e un cruscotto di monitoraggio delle trasformazioni eseguite. 

<img src="src/docs/img/ricerca_trasformazioni.png"> 

<img src="src/docs/img/inserimento_trasformazioni.png"> 


# Librerie utilizzate

|  GroupId | ArtifactId  | Version  | Type   |  Licenses |
|---|---|---|---|---|
|antlr|antlr|2.7.7.redhat-7|jar|BSD License|
ch.qos.logback|logback-classic|1.2.1|jar|Eclipse Public License - v 1.0, GNU Lesser General Public License|
|com.codeborne|phantomjsdriver|1.4.4|jar|The BSD 2-Clause License|
|com.fasterxml|classmate|1.5.1.redhat-00001|jar|Apache License, Version 2.0|
|com.fasterxml.jackson.core|jackson-annotations|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.core|jackson-core|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.core|jackson-databind|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.datatype|jackson-datatype-jdk8|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.datatype|jackson-datatype-jsr310|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.jaxrs|jackson-jaxrs-base|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.jaxrs|jackson-jaxrs-json-provider|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.jackson.module|jackson-module-jaxb-annotations|2.12.7.redhat-00003|jar|The Apache Software License, Version 2.0|
|com.fasterxml.woodstox|woodstox-core|6.4.0.redhat-00001|jar|The Apache License, Version 2.0|
|com.github.ben-manes.caffeine|caffeine|2.8.8.redhat-00001|jar|Apache License, Version 2.0|
|com.github.fge|json-patch|1.9.0.redhat-00002|jar|The Apache License, Version 2.0, Lesser General Public License, version 3 or greater|
|com.google.code.gson|gson|2.8.9.redhat-00001|jar|Apache-2.0|
|com.google.guava|failureaccess|1.0.1.redhat-00002|jar|Apache License, Version 2.0|
|com.google.guava|guava|30.1.0.redhat-00001|jar|Apache License, Version 2.0|
|com.google.inject|guice|4.2.1|no_aop|jar|The Apache Software License, Version 2.0|
|com.h2database|h2|1.4.197.redhat-00004|jar|MPL 2.0, EPL 1.0|
|com.sun.activation|jakarta.activation|1.2.2.redhat-00001|jar|EDL 1.0|
|com.sun.istack|istackcommons-runtime|3.0.10.redhat-00001|jar|Eclipse Distribution License - v 1.0|
|com.sun.mail|jakarta.mail|1.6.7.redhat-00001|jar|EPL 2.0, GPL2 w/ CPE, EDL 1.0|
|commons-beanutils|commons-beanutils|1.9.4|jar|Apache License, Version 2.0|
|commons-cli|commons-cli|1.4|jar|Apache License, Version 2.0|
|commons-codec|commons-codec|1.15|jar|Apache License, Version 2.0|
|commons-fileupload|commons-fileupload|1.5|jar|Apache-2.0|
|commons-io|commons-io|2.12.0|jar|Apache-2.0|
|commons-jxpath|commons-jxpath|1.3|jar|The Apache Software License, Version 2.0|
|commons-logging|commons-logging|1.2|jar|The Apache Software License, Version 2.0|
|commons-net|commons-net|3.9.0|jar|Apache License, Version 2.0|
|io.netty|netty-handler|4.1.86.Final-redhat-00001|jar|Apache License, Version 2.0|
|io.netty|netty-transport-native-epoll|4.1.86.Final-redhat-00001|jar|Apache License, Version 2.0|
|io.reactivex.rxjava3|rxjava|3.0.9.redhat-00001|jar||
|io.undertow|undertow-core|2.2.24.SP1-redhat-00001|jar|Apache License Version 2.0|
|it.eng.parer|idp-jaas-rdbms|0.0.9|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|parer-kettle-soap-client|1.1.1|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|preingest-xml|3.0.16|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacer-xml|2.7.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacerping-jboss-ejb|5.1.0|ejb|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacerping-jboss-jpa|5.1.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacerping-jboss-slg|5.1.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacerping-jboss-web|5.1.0|pom|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|sacerping-jboss-web|5.1.0|war|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-core|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-middle|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-paginator-ejb|5.12.0|ejb|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-paginator-gf|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-si-client|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-si-server|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-si-util|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-sl-ejb|5.12.0|ejb|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-sl-jpa|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-sl-slg|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-sl-web|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-sl-web|5.12.0|war|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-timer-wrapper-common|5.12.0|jar|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|it.eng.parer|spagofat-timer-wrapper-ejb|5.12.0|ejb|GNU AFFERO GENERAL PUBLIC LICENSE Version 3|
|jakarta.enterprise|jakarta.enterprise.cdi-api|2.0.2.redhat-00002|jar|Apache License 2.0|
|jakarta.inject|jakarta.inject-api|1.0.3.redhat-00001|jar|The Apache Software License, Version 2.0|
|jakarta.json|jakarta.json-api|1.1.6.redhat-00001|jar|Eclipse Public License 2.0, GNU General Public License, version 2 with the GNU Classpath Exception|
|jakarta.json.bind|jakarta.json.bind-api|1.0.2.redhat-00001|jar|Eclipse Public License 2.0, GNU General Public License, version 2 with the GNU Classpath Exception|
|jakarta.persistence|jakarta.persistence-api|2.2.3.redhat-00001|jar|Eclipse Public License v. 2.0, Eclipse Distribution License v. 1.0|
|jakarta.security.enterprise|jakarta.security.enterprise-api|1.0.2.redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|jakarta.validation|jakarta.validation-api|2.0.2.redhat-00001|jar|Apache License 2.0|
javax.annotation|jsr250-api|1.0|jar|COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0|
javax.inject|javax.inject|1|jar|The Apache Software License, Version 2.0|
javax.jws|jsr181-api|1.0.0.MR1-redhat-8|jar|CDDL+GPLv2|
javax.servlet|javax.servlet-api|4.0.1|jar|CDDL + GPLv2 with classpath exception|
joda-time|joda-time|2.12.5|jar|Apache License, Version 2.0|
|net.bytebuddy|byte-buddy|1.11.12.redhat-00002|jar|Apache License, Version 2.0|
|net.java.xadisk|xadisk|1.2.2.5|jar|
|net.sourceforge.javacsv|javacsv|2.0|jar|GNU Library or Lesser General Public License|
|org.apache.commons|commons-collections4|4.4|jar|Apache License, Version 2.0|
|org.apache.commons|commons-compress|1.23.0|jar|Apache-2.0|
|org.apache.commons|commons-lang3|3.12.0|jar|Apache License, Version 2.0|
|org.apache.commons|commons-text|1.10.0|jar|Apache License, Version 2.0|
|org.apache.cxf|cxf-api|2.7.18.SP6-redhat-1|jar|The Apache Software License, Version 2.0|
|org.apache.cxf|cxf-rt-bindings-soap|3.3.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.cxf|cxf-rt-bindings-xml|3.3.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.cxf|cxf-rt-core|2.7.18.SP6-redhat-1|jar|The Apache Software License, Version 2.0|
|org.apache.cxf|cxf-rt-frontend-jaxws|3.3.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.cxf|cxf-rt-frontend-simple|3.3.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.cxf|cxf-rt-transports-http|3.3.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.cxf|cxf-rt-ws-addr|3.3.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.cxf|cxf-rt-ws-policy|3.3.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.httpcomponents|httpclient|4.5.14|jar|Apache License, Version 2.0|
|org.apache.httpcomponents|httpcore|4.4.16|jar|Apache License, Version 2.0|
|org.apache.httpcomponents|httpmime|4.5.14|jar|Apache License, Version 2.0|
|org.apache.james|apache-mime4j-dom|0.8.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.james|apache-mime4j-storage|0.8.9.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.lucene|lucene-analyzers-common|5.5.5.redhat-2|jar|Apache 2|
|org.apache.lucene|lucene-core|5.5.5.redhat-2|jar|Apache 2|
|org.apache.lucene|lucene-facet|5.5.5.redhat-2|jar|Apache 2|
|org.apache.lucene|lucene-misc|5.5.5.redhat-2|jar|Apache 2|
|org.apache.lucene|lucene-queries|5.5.5.redhat-2|jar|Apache 2|
|org.apache.lucene|lucene-queryparser|5.5.5.redhat-2|jar|Apache 2|
|org.apache.maven|maven-artifact|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-builder-support|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-compat|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-core|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-embedder|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-model|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-model-builder|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-plugin-api|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-repository-metadata|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-resolver-provider|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-settings|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-settings-builder|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven|maven-slf4j-provider|3.6.3|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-api|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-connector-basic|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-impl|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-spi|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-transport-wagon|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.resolver|maven-resolver-util|1.4.1|jar|Apache License, Version 2.0|
|org.apache.maven.shared|maven-shared-utils|3.2.1|jar|Apache License, Version 2.0|
|org.apache.maven.wagon|wagon-file|3.3.4|jar|Apache License, Version 2.0|
|org.apache.maven.wagon|wagon-http|3.3.4|jar|Apache License, Version 2.0|
|org.apache.maven.wagon|wagon-provider-api|3.3.4|jar|Apache License, Version 2.0|
|org.apache.poi|poi|4.1.2|jar|Apache License, Version 2.0|
|org.apache.santuario|xmlsec|2.2.3.redhat-00001|jar|Apache License, Version 2.0|
|org.apache.taglibs|taglibs-standard-impl|1.2.6.RC1-redhat-1|jar|Apache License, Version 2.0|
|org.apache.taglibs|taglibs-standard-spec|1.2.6.RC1-redhat-1|jar|Apache License, Version 2.0|
|org.apache.tika|tika-core|2.8.0|jar|Apache-2.0|
|org.apache.xmlbeans|xmlbeans|3.1.0|jar|The Apache Software License, Version 2.0|
|org.codehaus.jackson|jackson-core-asl|1.9.13.redhat-00007|jar|The Apache Software License, Version 2.0|
|org.codehaus.jackson|jackson-jaxrs|1.9.13.redhat-00007|jar|The Apache Software License, Version 2.0, GNU Lesser General Public License (LGPL), Version 2.1|
|org.codehaus.jackson|jackson-mapper-asl|1.9.13.redhat-00007|jar|The Apache Software License, Version 2.0|
|org.codehaus.jackson|jackson-xc|1.9.13.redhat-00007|jar|The Apache Software License, Version 2.0, GNU Lesser General Public License (LGPL), Version 2.1|
|org.codehaus.jettison|jettison|1.5.4|jar|Apache License, Version 2.0|
|org.codehaus.plexus|plexus-classworlds|2.6.0|jar|Apache License, Version 2.0|
|org.codehaus.plexus|plexus-component-annotations|2.1.0|jar|Apache License, Version 2.0|
|org.codehaus.plexus|plexus-interpolation|1.25|jar|Apache License, Version 2.0|
|org.codehaus.plexus|plexus-utils|3.2.1|jar|Apache License, Version 2.0|
|org.codehaus.woodstox|stax2-api|4.2.1.redhat-00001|jar|The BSD License|
|org.eclipse.sisu|org.eclipse.sisu.inject|0.3.4|jar|Eclipse Public License, Version 1.0|
|org.eclipse.sisu|org.eclipse.sisu.plexus|0.3.4|jar|Eclipse Public License, Version 1.0|
|org.fusesource.jansi|jansi|1.17.1|jar|The Apache Software License, Version 2.0|
|org.glassfish.jaxb|jaxb-runtime|2.3.3.b02-redhat-00002|jar|Eclipse Distribution License - v 1.0|
|org.glassfish.jaxb|txw2|2.3.3.b02-redhat-00002|jar|Eclipse Distribution License - v 1.0|
|org.hibernate|hibernate-envers|5.3.29.Final-redhat-00001|jar|GNU Library General Public License v2.1 or later|
|org.hibernate|hibernate-search-engine|5.10.13.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.hibernate|hibernate-search-orm|5.10.13.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.hibernate.common|hibernatecommons-annotations|5.0.5.Final-redhat-00002|jar|GNU Lesser General Public License v2.1 or later|
|org.hibernate.validator|hibernate-validator-annotation-processor|6.0.23.Final-redhat-00001|jar|Apache License 2.0|
|org.infinispan|infinispan-client-hotrod|11.0.17.Final-redhat-00001|jar|Apache License 2.0|
|org.infinispan|infinispan-commons|11.0.17.Final-redhat-00001|jar|Apache License 2.0|
|org.infinispan|infinispan-core|11.0.17.Final-redhat-00001|jar|Apache License 2.0|
|org.infinispan|infinispan-directory-provider|10.1.8.Final-redhat-00001|jar|Apache License 2.0|
|org.infinispan.protostream|protostream|4.3.5.Final-redhat-00001|jar|Apache License 2.0|
|org.javassist|javassist|3.27.0.GA-redhat-00001|jar|MPL 1.1, LGPL 2.1, Apache License 2.0|
|org.jboss|jandex|2.4.2.Final-redhat-00001|jar|Apache License, Version 2.0|
|org.jboss.arquillian.config|arquillian-config-api|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.config|arquillian-config-impl-base|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.config|arquillian-config-spi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.container|arquillian-container-impl-base|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.container|arquillian-container-spi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.container|arquillian-container-test-api|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.container|arquillian-container-test-impl-base|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.container|arquillian-container-test-spi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.core|arquillian-core-api|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.core|arquillian-core-impl-base|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.core|arquillian-core-spi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.extension|arquillian-drone-api|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-appium-extension|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-browserstack-extension|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-configuration|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-impl|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-saucelabs-extension|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-spi|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-webdriver|2.5.2|jar|Public Domain|
|org.jboss.arquillian.extension|arquillian-drone-webdriver-depchain|2.5.2|pom|Public Domain|
|org.jboss.arquillian.graphene|graphene-webdriver|2.3.2|pom|GNU Lesser General Public License, Version 2.1|
|org.jboss.arquillian.junit|arquillian-junit-container|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.junit|arquillian-junit-core|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.junit|arquillian-junit-standalone|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.protocol|arquillian-protocol-jmx|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.protocol|arquillian-protocol-servlet|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.test|arquillian-test-api|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.test|arquillian-test-impl-base|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.test|arquillian-test-spi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testenricher|arquillian-testenricher-cdi|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testenricher|arquillian-testenricher-ejb|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testenricher|arquillian-testenricher-initialcontext|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testenricher|arquillian-testenricher-resource|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testng|arquillian-testng-container|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testng|arquillian-testng-core|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.arquillian.testng|arquillian-testng-standalone|1.6.0.Final|jar|Apache License, Version 2.0|
|org.jboss.eap|wildfly-clustering-api|7.4.11.GA-redhat-00002|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.eap|wildfly-clustering-service|7.4.11.GA-redhat-00002|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.eap|wildfly-clustering-singleton-api|7.4.11.GA-redhat-00002|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.eap|wildfly-ejb-client-bom|7.4.11.GA|pom|Apache License 2.0|
|org.jboss.eap|wildfly-jaxws-client-bom|7.4.11.GA|pom|Apache License 2.0|
|org.jboss.eap|wildfly-jms-client-bom|7.4.11.GA|pom|Apache License 2.0|
|org.jboss.eap|wildfly-security-api|7.4.11.GA-redhat-00002|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.ejb3|jboss-ejb3-ext-api|2.3.0.Final-redhat-00001|jar|Public Domain|
|org.jboss.logging|commons-logging-jboss-logging|1.0.0.Final-redhat-1|jar|Apache License 2.0|
|org.jboss.logging|jboss-logging|3.4.1.Final-redhat-00001|jar|Apache License, version 2.0|
|org.jboss.msc|jboss-msc|1.4.12.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 only|
|org.jboss.narayana.xts|jbossxts|5.11.4.Final-redhat-00001|jar|LGPL 2.1|
|org.jboss.resteasy|resteasy-atom-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-client|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jackson-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jackson2-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jaxb-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jaxrs|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jettison-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-jsapi|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-json-p-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-multipart-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-spring|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.resteasy|resteasy-validator-provider|3.15.7.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.security|jboss-negotiation-common|3.0.6.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.security|jboss-negotiation-extras|3.0.6.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.security|jboss-negotiation-ntlm|3.0.6.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.security|jboss-negotiation-spnego|3.0.6.Final-redhat-00001|jar|GNU Lesser General Public License v2.1 or later|
|org.jboss.security|jbossxacml|2.0.8.Final-redhat-8|jar|lgpl|
|org.jboss.shrinkwrap|shrinkwrap-api|1.2.6|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-api-nio2|1.2.6|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-depchain|1.2.6|pom|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-depchain-java7|1.2.6|pom|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-impl-base|1.2.6|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-impl-nio2|1.2.6|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap|shrinkwrap-spi|1.2.6|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-api-base|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-api-javaee|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-api-jboss|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-depchain|2.0.0|pom|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-gen|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-impl-base|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-impl-javaee|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-impl-jboss|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.descriptors|shrinkwrap-descriptors-spi|2.0.0|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-api|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-api-gradle-embedded-archive|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-api-maven|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-api-maven-archive|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-api-maven-embedded|3.1.4|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-depchain|2.2.7|pom|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-gradle-depchain|2.2.7|pom|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-impl-gradle-embedded-archive|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-impl-maven|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-impl-maven-archive|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-impl-maven-embedded|3.1.4|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-spi|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.shrinkwrap.resolver|shrinkwrap-resolver-spi-maven|2.2.7|jar|Apache License, Version 2.0|
|org.jboss.spec|jboss-jakartaee-8.0|1.0.1.Final-redhat-00007|pom|GNU Lesser General Public License, Version 2.1|
|org.jboss.spec.javax.annotation|jboss-annotations-api_1.3_spec|2.0.1.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.batch|jboss-batch-api_1.0_spec|2.0.0.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.spec.javax.ejb|jboss-ejb-api_3.2_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.el|jboss-el-api_3.0_spec|2.0.1.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE, Apache 2.0, LGPL 2.1 or later|
|org.jboss.spec.javax.enterprise.concurrent|jboss-concurrency-api_1.0_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.faces|jboss-jsf-api_2.3_spec|3.0.0.SP07-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.interceptor|jboss-interceptors-api_1.2_spec|2.0.0.Final-redhat-00002|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.jms|jboss-jms-api_2.0_spec|2.0.0.Final-redhat-00001|jar|Eclipse Public License 2.0, GNU General Public License, version 2 with the GNU Classpath Exception|
|org.jboss.spec.javax.management.j2ee|jboss-j2eemgmt-api_1.1_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.resource|jboss-connector-api_1.7_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.security.auth.message|jboss-jaspi-api_1.1_spec|2.0.1.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.security.jacc|jboss-jacc-api_1.5_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.servlet|jboss-servlet-api_4.0_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.servlet.jsp|jboss-jsp-api_2.3_spec|2.0.0.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.transaction|jboss-transaction-api_1.3_spec|2.0.0.Final-redhat-00005|jar|Eclipse Public License 2.0, GNU General Public License, Version 2 with the Classpath Exception|
|org.jboss.spec.javax.websocket|jboss-websocket-api_1.1_spec|2.0.0.Final-redhat-00001|jar|Eclipse Public License 2.0, GNU General Public License, version 2 with the GNU Classpath Exception|
|org.jboss.spec.javax.ws.rs|jboss-jaxrs-api_2.1_spec|2.0.1.Final-redhat-00001|jar|EPL 2.0, GPL2 w/ CPE|
|org.jboss.spec.javax.xml.bind|jboss-jaxb-api_2.3_spec|2.0.1.Final-redhat-00001|jar|Eclipse Distribution License - v 1.0|
|org.jboss.spec.javax.xml.soap|jboss-saaj-api_1.4_spec|1.0.2.Final-redhat-00002|jar|Eclipse Distribution License, Version 1.0|
|org.jboss.spec.javax.xml.ws|jboss-jaxws-api_2.3_spec|2.0.0.Final-redhat-00001|jar|Eclipse Distribution License - v 1.0|
|org.jboss.threads|jboss-threads|2.4.0.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.xnio|xnio-api|3.8.9.Final-redhat-00001|jar|Apache License 2.0|
|org.jboss.xnio|xnio-nio|3.8.9.Final-redhat-00001|jar|Apache License 2.0|
|org.jgroups|jgroups|4.2.15.Final-redhat-00001|jar|Apache License 2.0|
|org.jsoup|jsoup|1.12.1|jar|The MIT License|
|org.keycloak|keycloak-servlet-filter-adapter|21.1.1|jar|Apache License, Version 2.0|
|org.owasp.esapi|esapi|2.2.0.0|jar|BSD, Creative Commons 3.0 BY-SA|
|org.picketbox|picketbox|5.0.3.Final-redhat-00009|jar|GNU Lesser General Public License v2.1 only|
|org.picketbox|picketbox-commons|1.0.0.final-redhat-5|jar|lgpl|
|org.picketlink|picketlink-api|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.picketlink|picketlink-common|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.picketlink|picketlink-config|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.picketlink|picketlink-federation|2.5.5.SP12-redhat-00012|jar|Apache License, Version 2.0|
|org.picketlink|picketlink-idm-api|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.picketlink|picketlink-idm-impl|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.picketlink|picketlink-impl|2.5.5.SP12-redhat-00012|jar|Apache License 2.0|
|org.powermock|powermock-reflect|1.7.4|jar|The Apache Software License, Version 2.0|
|org.reactivestreams|reactive-streams|1.0.3.redhat-00003|jar|
|org.seleniumhq.selenium|htmlunit-driver|2.28|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|lift|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-api|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-chrome-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-edge-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-firefox-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-ie-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-java|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-leg-rc|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-opera-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-remote-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-safari-driver|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-server|3.11.0|jar|The Apache Software License, Version 2.0|
|org.seleniumhq.selenium|selenium-support|3.11.0|jar|The Apache Software License, Version 2.0|
|org.slf4j|jcl-over-slf4j|2.0.7|jar|Apache License, Version 2.0|
|org.slf4j|log4j-over-slf4j|2.0.7|jar|Apache Software Licenses|
|org.slf4j|slf4j-log4j12|2.0.7|jar|MIT License|
|org.slf4j|slf4j-simple|1.7.29|jar|MIT License|
|org.sonatype.plexus|plexus-cipher|1.7|jar|Apache Public License 2.0|
|org.sonatype.plexus|plexus-sec-dispatcher|1.4|jar|Apache Public License 2.0|
|org.springframework|spring-aop|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-context|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-context-support|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-core|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-jdbc|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-orm|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-test|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-tx|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-web|5.3.30|jar|Apache License, Version 2.0|
|org.springframework|spring-webmvc|5.3.30|jar|Apache License, Version 2.0|
|org.springframework.security|spring-security-config|5.8.8|jar|Apache License, Version 2.0|
|org.springframework.security|spring-security-core|5.8.8|jar|Apache License, Version 2.0|
|org.springframework.security|spring-security-web|5.8.8|jar|Apache License, Version 2.0|
|org.springframework.security.extensions|spring-security-saml2-core|1.0.10.RELEASE|jar|The Apache Software License, Version 2.0|
|org.testng|testng|6.11|jar|Apache 2.0|
|org.webjars|font-awesome|6.4.0|jar|CC BY 3.0|
|org.webjars|highlightjs|11.5.0|jar|BSD|
|org.webjars|jquery|3.6.4|jar|MIT License|
|org.webjars|jquery-ui|1.13.2|jar|MIT License|
|org.webjars|jstree|3.3.8|jar|MIT License, GPL|
|org.webjars|select2|4.0.13|jar|MIT|
|org.webjars.bower|chosen|1.8.7|jar|MIT|
|org.webjars.bowergithub.wcoder|highlightjs-line-numbers.js|2.7.0|jar|MIT|
|org.webjars.npm|highlightjs-badgejs|0.0.5|jar|MIT|
|org.wildfly.arquillian|wildfly-arquillian-common|3.0.1.Final|jar|Apache License Version 2.0|
|org.wildfly.arquillian|wildfly-arquillian-container-managed|3.0.1.Final|jar|Apache License Version 2.0|
|org.wildfly.arquillian|wildfly-arquillian-container-remote|3.0.1.Final|jar|Apache License Version 2.0|
|org.wildfly.client|wildfly-client-config|1.0.1.Final-redhat-00001|jar|Apache License 2.0|
|org.wildfly.common|wildfly-common|1.5.4.Final-redhat-00001|jar|Apache License 2.0|
|org.wildfly.discovery|wildfly-discovery-client|1.2.1.Final-redhat-00001|jar|Apache License 2.0|
|org.wildfly.security|wildfly-elytron|1.15.16.Final-redhat-00001|jar|Apache License 2.0|
|software.amazon.awssdk|accessanalyzer|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|account|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|acm|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|acmpca|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|alexaforbusiness|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|amp|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|amplify|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|amplifybackend|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|amplifyuibuilder|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|annotations|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|apache-client|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|apigateway|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|apigatewaymanagementapi|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|apigatewayv2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appconfig|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appconfigdata|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appflow|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appintegrations|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|applicationautoscaling|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|applicationcostprofiler|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|applicationdiscovery|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|applicationinsights|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appmesh|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|apprunner|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appstream|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|appsync|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|arczonalshift|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|arns|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|athena|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|auditmanager|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|auth|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|autoscaling|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|autoscalingplans|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-cbor-protocol|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-core|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-json-protocol|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-query-protocol|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|aws-xml-protocol|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|backup|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|backupgateway|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|backupstorage|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|batch|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|billingconductor|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|braket|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|budgets|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chime|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chimesdkidentity|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chimesdkmediapipelines|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chimesdkmeetings|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chimesdkmessaging|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|chimesdkvoice|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cleanrooms|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloud9|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudcontrol|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|clouddirectory|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudformation|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudfront|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudhsm|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudhsmv2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudsearch|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudsearchdomain|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudtrail|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudwatch|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudwatch-metric-publisher|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudwatchevents|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cloudwatchlogs|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codeartifact|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codebuild|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codecatalyst|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codecommit|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codedeploy|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegen|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegen-lite|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegen-lite-maven-plugin|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegen-maven-plugin|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codeguruprofiler|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codegurureviewer|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codepipeline|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codestar|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codestarconnections|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|codestarnotifications|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cognitoidentity|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cognitoidentityprovider|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|cognitosync|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|comprehend|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|comprehendmedical|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|computeoptimizer|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|config|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|connect|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|connectcampaigns|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|connectcases|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|connectcontactlens|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|connectparticipant|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|controltower|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|costandusagereport|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|costexplorer|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|customerprofiles|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|databasemigration|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|databrew|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|dataexchange|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|datapipeline|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|datasync|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|dax|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|detective|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|devicefarm|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|devopsguru|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|directconnect|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|directory|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|dlm|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|docdb|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|docdbelastic|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|drs|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|dynamodb|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|dynamodb-enhanced|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ebs|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ec2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ec2instanceconnect|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ecr|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ecrpublic|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ecs|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|efs|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|eks|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticache|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticbeanstalk|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticinference|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticloadbalancing|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticloadbalancingv2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elasticsearch|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|elastictranscoder|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|emr|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|emrcontainers|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|emrserverless|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|eventbridge|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|evidently|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|finspace|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|finspacedata|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|firehose|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|fis|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|fms|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|forecast|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|forecastquery|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|frauddetector|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|fsx|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|gamelift|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|gamesparks|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|glacier|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|globalaccelerator|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|glue|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|grafana|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|greengrass|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|greengrassv2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|groundstation|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|guardduty|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|health|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|healthlake|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|honeycode|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|http-client-spi|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iam|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|identitystore|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|imagebuilder|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|inspector|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|inspector2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iot|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iot1clickdevices|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iot1clickprojects|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotanalytics|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotdataplane|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotdeviceadvisor|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotevents|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ioteventsdata|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotfleethub|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotfleetwise|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotjobsdataplane|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotroborunner|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotsecuretunneling|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotsitewise|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotthingsgraph|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iottwinmaker|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|iotwireless|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ivs|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ivschat|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|json-utils|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kafka|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kafkaconnect|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kendra|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kendraranking|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|keyspaces|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesis|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisanalytics|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisanalyticsv2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisvideo|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisvideoarchivedmedia|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisvideomedia|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisvideosignaling|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kinesisvideowebrtcstorage|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|kms|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lakeformation|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lambda|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lexmodelbuilding|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lexmodelsv2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lexruntime|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lexruntimev2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|licensemanager|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|licensemanagerlinuxsubscriptions|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|licensemanagerusersubscriptions|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lightsail|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|location|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lookoutequipment|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lookoutmetrics|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|lookoutvision|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|m2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|machinelearning|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|macie|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|macie2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|managedblockchain|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|marketplacecatalog|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|marketplacecommerceanalytics|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|marketplaceentitlement|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|marketplacemetering|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediaconnect|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediaconvert|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|medialive|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediapackage|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediapackagevod|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediastore|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediastoredata|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mediatailor|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|memorydb|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|metrics-spi|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mgn|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|migrationhub|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|migrationhubconfig|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|migrationhuborchestrator|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|migrationhubrefactorspaces|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|migrationhubstrategy|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mobile|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mq|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mturk|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|mwaa|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|neptune|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|netty-nio-client|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|networkfirewall|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|networkmanager|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|nimble|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|oam|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|omics|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|opensearch|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|opensearchserverless|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|opsworks|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|opsworkscm|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|organizations|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|outposts|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|panorama|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|personalize|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|personalizeevents|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|personalizeruntime|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pi|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pinpoint|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pinpointemail|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pinpointsmsvoice|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pinpointsmsvoicev2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pipes|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|polly|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|pricing|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|privatenetworks|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|profiles|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|protocol-core|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|proton|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|qldb|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|qldbsession|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|quicksight|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ram|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rbin|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rds|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rdsdata|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|redshift|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|redshiftdata|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|redshiftserverless|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|regions|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rekognition|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|resiliencehub|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|resourceexplorer2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|resourcegroups|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|resourcegroupstaggingapi|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|robomaker|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rolesanywhere|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53domains|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53recoverycluster|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53recoverycontrolconfig|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53recoveryreadiness|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|route53resolver|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|rum|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|s3|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|s3-transfer-manager|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|s3control|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|s3outposts|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemaker|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakera2iruntime|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakeredge|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakerfeaturestoreruntime|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakergeospatial|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakermetrics|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sagemakerruntime|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|savingsplans|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|scheduler|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|schemas|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sdk-core|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|secretsmanager|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|securityhub|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|securitylake|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|serverlessapplicationrepository|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|servicecatalog|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|servicecatalogappregistry|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|servicediscovery|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|servicequotas|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ses|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sesv2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sfn|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|shield|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|signer|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|simspaceweaver|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sms|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|snowball|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|snowdevicemanagement|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sns|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sqs|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssm|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssmcontacts|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssmincidents|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssmsap|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sso|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssoadmin|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|ssooidc|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|storagegateway|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|sts|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|support|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|supportapp|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|swf|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|synthetics|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|textract|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|third-party-jackson-core|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|third-party-jackson-dataformat-cbor|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|timestreamquery|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|timestreamwrite|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|transcribe|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|transcribestreaming|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|transfer|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|translate|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|url-connection-client|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|utils|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|voiceid|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|waf|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|wafv2|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|wellarchitected|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|wisdom|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|workdocs|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|worklink|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|workmail|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|workmailmessageflow|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|workspaces|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|workspacesweb|2.19.26|jar|Apache License, Version 2.0|
|software.amazon.awssdk|xray|2.19.26|jar|Apache License, Version 2.0|
|xalan|xalan|2.7.2|jar|The Apache Software License, Version 2.0|
|xerces|xercesImpl|2.12.0|jar|The Apache Software License, Version 2.0|
|xml-apis|xml-apis|1.4.01|jar|The Apache Software License, Version 2.0, The SAX License, The W3C License|
|xml-resolver|xml-resolver|1.2|jar||


# Supporto

Mantainer del progetto è [Engineering Ingegneria Informatica S.p.A.](https://www.eng.it/).

# Contributi

Se interessati a crontribuire alla crescita del progetto potete scrivere all'indirizzo email <a href="mailto:areasviluppoparer@regione.emilia-romagna.it">areasviluppoparer@regione.emilia-romagna.it</a>.

# Credits

Progetto di proprietà di [Regione Emilia-Romagna](https://www.regione.emilia-romagna.it/) sviluppato a cura di [Engineering Ingegneria Informatica S.p.A.](https://www.eng.it/).

# Licenza

Questo progetto è rilasciato sotto licenza GNU Affero General Public License v3.0 or later ([LICENSE.txt](LICENSE.txt)).

# Appendice

## Documentazione aggiuntiva

Alcuni riferimenti:

* Sito ParER: https://poloarchivistico.regione.emilia-romagna.it/
* Manuale di conservazione: https://poloarchivistico.regione.emilia-romagna.it/documentazione/documenti_open/manualeconservazione_v5-0.pdf/@@download/file/ManualeConservazione_v2.0.pdf 
