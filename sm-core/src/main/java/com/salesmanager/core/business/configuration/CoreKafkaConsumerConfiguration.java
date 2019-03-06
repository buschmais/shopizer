package com.salesmanager.core.business.configuration;

import com.salesmanager.core.business.integration.catalog.dto.ProductDTO;
import com.salesmanager.core.business.integration.catalog.dto.ProductOptionDTO;
import com.salesmanager.core.business.integration.catalog.dto.ProductOptionValueDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class CoreKafkaConsumerConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public Map<String, Object> consumerConfiguration() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getRequiredProperty("core.spring.kafka.bootstrap-servers"));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                environment.getRequiredProperty("core.spring.kafka.consumer.auto-offset-reset"));
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                environment.getRequiredProperty("core.spring.kafka.consumer.group-id"));
        return props;
    }

    @Bean
    public ConsumerFactory<String, ProductDTO> productConsumerFactory() {

        return new DefaultKafkaConsumerFactory<>(consumerConfiguration(), new StringDeserializer(), new JsonDeserializer<>(ProductDTO.class));
    }

    @Bean
    public ConsumerFactory<String, ProductOptionDTO> productOptionConsumerFactory() {
        Map<String, Object> props = consumerConfiguration();
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(ProductOptionDTO.class));
    }

    @Bean
    public ConsumerFactory<String, ProductOptionValueDTO> productOptionValueConsumerFactory() {
        Map<String, Object> props = consumerConfiguration();
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(ProductOptionValueDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductDTO> productKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ProductDTO> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductOptionDTO> productOptionKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ProductOptionDTO> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productOptionConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductOptionValueDTO> productOptionValueKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ProductOptionValueDTO> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productOptionValueConsumerFactory());
        return factory;
    }
}
