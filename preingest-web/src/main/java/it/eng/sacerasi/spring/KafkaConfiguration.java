package it.eng.sacerasi.spring;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import it.eng.parer.spring.cdi.starter.kafka.MessageSenderBean;
import it.eng.sacerasi.job.coda.spring.PayloadManagerBean;
import it.eng.sacerasi.job.consumerCodaVerificaH.spring.ConsumerCodaVHBean;
import it.eng.sacerasi.job.consumerCodaVers.spring.ConsumerCoda1Bean;
import it.eng.sacerasi.job.consumerCodaVers.spring.ConsumerCoda2Bean;
import it.eng.sacerasi.job.consumerCodaVers.spring.ConsumerCoda3Bean;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.backoff.ExponentialBackOff;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@EnableKafka
@EnableTransactionManagement(proxyTargetClass = true)
// proxyTargetClass=true (CGLIB) è necessario: Spring 5 rileva JEE @TransactionAttribute
// sugli EJB cercati via JNDI (JndiObjectFactoryBean) e tenta di avvolgerli in proxy
// transazionali. Con JDK proxy mode (false) il proxy risultante non estende la classe
// concreta dell'EJB → BeanNotOfRequiredTypeException all'injection via @EJB.
// Con CGLIB mode il proxy subclassa il proxy WildFly (che a sua volta subclassa l'EJB)
// → rimane assignable alla classe concreta. ✓
// @KafkaListener funziona correttamente con CGLIB (usa getTargetClass(), non isJdkDynamicProxy).
@Configuration
@ComponentScan(basePackages = {
        // Bridge CDI↔Spring (spring-cdi-kafka-starter):
        // registra ApplicationContextProvider che rende il KafkaTemplate disponibile
        // ai bean CDI tramite @Inject KafkaTemplateCDI.
        // Mantenuto attivo anche se non attualmente usato: consente a qualsiasi EJB
        // di aggiungere @Inject KafkaTemplateCDI senza ulteriori modifiche alla configurazione.
        "it.eng.parer.spring.cdi.core"
})
public class KafkaConfiguration {

    @Value("#{systemProperties['kafka.prefix']}")
    private String kafkaPrefix;

    @Value("#{systemProperties['kafka.bootstrap.servers']}")
    private String kafkaBootstrapServers;

    @Value("#{systemProperties['kafka.sasl.username']}")
    private String kafkaSaslUsername;

    @Value("#{systemProperties['kafka.sasl.password']}")
    private String kafkaSaslPassword;

    @Value("#{systemProperties['kafka.schema.registry.url']}")
    private String kafkaSchemaRegistryUrl;

    @Value("#{systemProperties['kafka.partitions']}")
    private Integer kafkaPartitions;

    @Value("#{systemProperties['kafka.replicas']}")
    private Integer kafkaReplicas;

    // =========================================================================
    // ADMIN + TOPIC PROVISIONING
    // =========================================================================

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        putSaslConfig(configs);
        return new KafkaAdmin(configs);
    }

    @Bean
    public KafkaAdmin.NewTopics topicCodaVers() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(kafkaPrefix + ".sacer_ping.coda1.raw")
                        .partitions(kafkaPartitions).replicas(kafkaReplicas).build(),
                TopicBuilder.name(kafkaPrefix + ".sacer_ping.coda2.raw")
                        .partitions(kafkaPartitions).replicas(kafkaReplicas).build(),
                TopicBuilder.name(kafkaPrefix + ".sacer_ping.coda3.raw")
                        .partitions(kafkaPartitions).replicas(kafkaReplicas).build(),
                // Dead-Letter Topics — retention infinita: i messaggi non vengono mai eliminati
                TopicBuilder.name(kafkaPrefix + ".sacer_ping.coda1.raw.DLT")
                        .partitions(kafkaPartitions).replicas(kafkaReplicas)
                        .config(TopicConfig.RETENTION_MS_CONFIG, "-1")
                        .config(TopicConfig.RETENTION_BYTES_CONFIG, "-1").build(),
                TopicBuilder.name(kafkaPrefix + ".sacer_ping.coda2.raw.DLT")
                        .partitions(kafkaPartitions).replicas(kafkaReplicas)
                        .config(TopicConfig.RETENTION_MS_CONFIG, "-1")
                        .config(TopicConfig.RETENTION_BYTES_CONFIG, "-1").build(),
                TopicBuilder.name(kafkaPrefix + ".sacer_ping.coda3.raw.DLT")
                        .partitions(kafkaPartitions).replicas(kafkaReplicas)
                        .config(TopicConfig.RETENTION_MS_CONFIG, "-1")
                        .config(TopicConfig.RETENTION_BYTES_CONFIG, "-1").build());
    }

    @Bean
    public KafkaAdmin.NewTopics topicVerificaHash() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(kafkaPrefix + ".sacer_ping.coda_ver_hash.raw")
                        .partitions(kafkaPartitions).replicas(kafkaReplicas).build(),
                TopicBuilder.name(kafkaPrefix + ".sacer_ping.coda_ver_hash.raw.DLT")
                        .partitions(kafkaPartitions).replicas(kafkaReplicas)
                        .config(TopicConfig.RETENTION_MS_CONFIG, "-1")
                        .config(TopicConfig.RETENTION_BYTES_CONFIG, "-1").build());
    }

    // =========================================================================
    // ERROR HANDLING — DefaultErrorHandler + DeadLetterPublishingRecoverer
    // Gestisce i retry con backoff esponenziale e il routing finale su DLT.
    // =========================================================================

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
        ExponentialBackOff backOff = new ExponentialBackOff(1_000L, 2.0);
        // 1s → 2s → 4s → elapsed 7s → STOP → DLT (3 tentativi totali)
        backOff.setMaxElapsedTime(7_000L);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
        handler.addNotRetryableExceptions(SecurityException.class, IllegalStateException.class);
        return handler;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            KafkaTemplate<String, Object> kafkaJsonTemplate) {
        return new DeadLetterPublishingRecoverer(
                kafkaJsonTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
    }

    // =========================================================================
    // LISTENER CONTAINER FACTORY
    // AckMode.RECORD: commit dell'offset dopo ogni singolo record elaborato.
    // =========================================================================

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            DefaultErrorHandler errorHandler,
            ConcurrentTaskExecutor jbossTaskExecutor) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setListenerTaskExecutor(jbossTaskExecutor);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    // =========================================================================
    // CONSUMER FACTORY
    // =========================================================================

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);

        // Il group.id NON viene impostato qui: ogni @KafkaListener deve specificarlo
        // esplicitamente tramite l'attributo groupId dell'annotazione.

        // ErrorHandlingDeserializer come wrapper: cattura gli errori di deserializzazione
        // e li gestisce tramite il DefaultErrorHandler anziché far crashare il container.
        //
        // KafkaAvroDeserializer (Confluent) come delegate: recupera lo schema da Schema Registry
        // e deserializza il payload Avro in GenericRecord.
        // I consumer accedono ai campi direttamente via GenericRecord.get("NOME_CAMPO").
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                KafkaAvroDeserializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                kafkaSchemaRegistryUrl);
        props.put(AbstractKafkaSchemaSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY,
                io.confluent.kafka.serializers.subject.TopicNameStrategy.class);

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // read_committed: visibili solo i messaggi prodotti in transazioni committed
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        // TIMEOUT_VERS_SACER = 3600s → max.poll.interval.ms >= 1h + margine
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 3_900_000);

        // max.poll.records = 1: un record per poll, evita che batch grandi saturino
        // max.poll.interval.ms durante elaborazioni lunghe (es. versamento a SACER)
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);

        putSaslConfig(props);
        return props;
    }

    // =========================================================================
    // EXECUTOR — ManagedExecutorService JBoss via JNDI
    // =========================================================================

    @Bean
    public ConcurrentTaskExecutor jbossTaskExecutor() throws Exception {
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiName("java:jboss/ee/concurrency/executor/default");
        factoryBean.afterPropertiesSet();
        Executor executor = (Executor) factoryBean.getObject();
        return new ConcurrentTaskExecutor(executor);
    }

    // =========================================================================
    // JPA — EntityManagerFactory via JNDI
    // Necessario per @PersistenceContext nei consumer bean Spring.
    // =========================================================================

    @Bean
    public JndiObjectFactoryBean myEmf() {
        JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:/MyEntityManagerFactory");
        // proxyInterface: espone il bean come EntityManagerFactory in modo che
        // PersistenceAnnotationBeanPostProcessor lo rilevi per @PersistenceContext
        jndi.setProxyInterface(EntityManagerFactory.class);
        return jndi;
    }

    @Bean
    public SharedEntityManagerBean entityManager(EntityManagerFactory myEmf) {
        SharedEntityManagerBean bean = new SharedEntityManagerBean();
        bean.setEntityManagerFactory(myEmf);
        return bean;
    }

    // =========================================================================
    // CONSUMER BEANS
    // =========================================================================

    @Bean
    public ConsumerCodaVHBean consumerCodaVHBean() {
        return new ConsumerCodaVHBean();
    }

    @Bean
    public ConsumerCoda1Bean consumerCoda1Bean(PayloadManagerBean payloadManagerHelper) {
        return new ConsumerCoda1Bean(payloadManagerHelper);
    }

    @Bean
    public ConsumerCoda2Bean consumerCoda2Bean(PayloadManagerBean payloadManagerHelper) {
        return new ConsumerCoda2Bean(payloadManagerHelper);
    }

    @Bean
    public ConsumerCoda3Bean consumerCoda3Bean(PayloadManagerBean payloadManagerHelper) {
        return new ConsumerCoda3Bean(payloadManagerHelper);
    }

    @Bean
    public PayloadManagerBean payloadManagerBean() {
        return new PayloadManagerBean();
    }

    // =========================================================================
    // EJB WRAPPERS — lookup JNDI degli EJB necessari ai consumer Spring
    // =========================================================================

    @Bean
    public JndiObjectFactoryBean codaHelper() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/CodaHelper");
        return jndi;
    }

    @Bean
    public JndiObjectFactoryBean salvataggioDati() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/SalvataggioDati");
        return jndi;
    }

    @Bean
    public JndiObjectFactoryBean verificaHashAsync() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/VerificaHashAsyncEjb");
        return jndi;
    }

    @Bean
    public JndiObjectFactoryBean commonDb() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/CommonDb");
        return jndi;
    }

    @Bean
    public JndiObjectFactoryBean configurationHelper() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/ConfigurationHelper");
        return jndi;
    }

    @Bean
    public JndiObjectFactoryBean strumentiUrbanisticiHelper() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/StrumentiUrbanisticiHelper");
        return jndi;
    }

    @Bean
    public JndiObjectFactoryBean sismaEjb() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/SismaEjb");
        return jndi;
    }

    @Bean
    public JndiObjectFactoryBean sismaHelper() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/SismaHelper");
        return jndi;
    }

    @Bean
    public JndiObjectFactoryBean backendHelper() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/BackendHelper");
        return jndi;
    }

    @Bean
    public JndiObjectFactoryBean richiestaSacerHelper() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/RichiestaSacerHelper");
        return jndi;
    }

    @Bean
    public JndiObjectFactoryBean messaggiHelper() {
        final JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName("java:app/SacerAsync-ejb/MessaggiHelper");
        return jndi;
    }

    // =========================================================================
    // TRANSACTION MANAGER — JTA delegato al container JBoss
    // =========================================================================

    @Bean
    @Primary
    public PlatformTransactionManager platformTransactionManager() {
        return new JtaTransactionManager();
    }

    // =========================================================================
    // PRODUCER FACTORY + KAFKA TEMPLATE
    // Usato esclusivamente dal DeadLetterPublishingRecoverer per scrivere sui topic .DLT.
    //
    // DelegatingByTypeSerializer: serializza il valore in base al tipo a runtime:
    // - byte[] → ByteArraySerializer (record con DeserializationException già serializzato)
    // - Object → KafkaAvroSerializer (record fallito nel listener)
    // L'ordine in LinkedHashMap è garantito: byte[] più specifico viene valutato per primo.
    // =========================================================================

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<Class<?>, Serializer> serializers = new LinkedHashMap<>();
        serializers.put(byte[].class, new ByteArraySerializer());
        serializers.put(Object.class, new KafkaAvroSerializer());

        DelegatingByTypeSerializer delegatingSerializer = new DelegatingByTypeSerializer(
                serializers, true);

        return new DefaultKafkaProducerFactory<>(
                senderProps(),
                new StringSerializer(),
                delegatingSerializer);
    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                kafkaSchemaRegistryUrl);
        putSaslConfig(props);
        return props;
    }

    /**
     * Aggiunge le proprietà SASL/PLAIN alla mappa di configurazione Kafka fornita. Usato da admin,
     * consumer e producer per condividere la stessa configurazione di sicurezza.
     */
    private void putSaslConfig(Map<String, Object> props) {
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", String.format(
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                kafkaSaslUsername, kafkaSaslPassword));
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaJsonTemplate(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // =========================================================================
    // BRIDGE CDI↔Spring
    // Espone il KafkaTemplate al contesto CDI tramite ApplicationContextProvider.
    // Necessario per abilitare @Inject KafkaTemplateCDI negli EJB che volessero
    // produrre messaggi Kafka direttamente (senza outbox pattern).
    // Mantenuto attivo come infrastruttura latente: costo minimo, disponibilità immediata.
    // =========================================================================

    @Bean
    public MessageSenderBean messageSenderBean() {
        return new MessageSenderBean();
    }

}
