package com.salesmanager.catalog.configuration;

import com.salesmanager.catalog.business.integration.core.dto.CustomerDTO;
import com.salesmanager.catalog.business.integration.core.dto.LanguageDTO;
import com.salesmanager.catalog.business.integration.core.dto.MerchantStoreDTO;
import com.salesmanager.catalog.business.integration.core.dto.TaxClassDTO;
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


    @Bean
    public ConsumerFactory<String, CustomerDTO> customerConsumerFactory() {
        Map<String, Object> props = consumerConfiguration();
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(CustomerDTO.class));
    }

    @Bean
    public ConsumerFactory<String, LanguageDTO> languageConsumerFactory() {
        Map<String, Object> props = consumerConfiguration();
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(LanguageDTO.class));
    }

    @Bean
    public ConsumerFactory<String, MerchantStoreDTO> merchantConsumerFactory() {
        Map<String, Object> props = consumerConfiguration();
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(MerchantStoreDTO.class));
    }

    @Bean
    public ConsumerFactory<String, TaxClassDTO> taxConsumerFactory() {
        Map<String, Object> props = consumerConfiguration();
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(TaxClassDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CustomerDTO> customerKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, CustomerDTO> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(customerConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LanguageDTO> languageKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, LanguageDTO> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(languageConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MerchantStoreDTO> merchantStoreKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, MerchantStoreDTO> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(merchantConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaxClassDTO> taxClassKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, TaxClassDTO> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taxConsumerFactory());
        return factory;
    }

}
