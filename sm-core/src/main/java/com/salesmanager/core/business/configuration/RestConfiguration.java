package com.salesmanager.core.business.configuration;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

@Configuration
public class RestConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public RestTemplate catalogRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(6);
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.custom().setConnectionManager(connectionManager).build()));
        String coreUrl = environment.getProperty("catalog.url");
        ((DefaultUriTemplateHandler) restTemplate.getUriTemplateHandler()).setBaseUrl(coreUrl);
        return restTemplate;
    }

}
