package com.salesmanager.catalog.configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.HazelcastInstanceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

@Configuration
@EnableHazelcastHttpSession
public class HazelcastConfiguration {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        UserCodeDeploymentConfig userCodeDeploymentConfig = config.getUserCodeDeploymentConfig();
        userCodeDeploymentConfig.setEnabled( true )
                .setClassCacheMode( UserCodeDeploymentConfig.ClassCacheMode.ETERNAL )
                .setProviderMode( UserCodeDeploymentConfig.ProviderMode.LOCAL_CLASSES_ONLY );
        return HazelcastInstanceFactory.newHazelcastInstance(config);
    }

}
