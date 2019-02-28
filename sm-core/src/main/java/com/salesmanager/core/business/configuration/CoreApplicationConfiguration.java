package com.salesmanager.core.business.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {"com.salesmanager.core.model"})
@ComponentScan({"com.salesmanager.core.business.services","com.salesmanager.core.business.utils","com.salesmanager.core.business.integration","com.salesmanager.core.integration"})
@EnableJpaRepositories(
        basePackages = {"com.salesmanager.core.business.repositories"},
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
@ImportResource("classpath:/spring/shopizer-core-context.xml")
@PropertySource("classpath:shopizer-core.properties")
@Import({CoreKafkaConsumerConfiguration.class, RestConfiguration.class})
public class CoreApplicationConfiguration {
}
