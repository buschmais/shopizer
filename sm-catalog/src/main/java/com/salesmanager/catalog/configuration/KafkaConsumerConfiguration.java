package com.salesmanager.catalog.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public Map<String, Object> consumerConfiguration() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getRequiredProperty("catalog.spring.kafka.bootstrap-servers"));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                environment.getRequiredProperty("catalog.spring.kafka.consumer.auto-offset-reset"));
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                environment.getRequiredProperty("catalog.spring.kafka.consumer.group-id"));
        return props;
    }

}
