package com.salesmanager.catalog.configuration;

import com.salesmanager.catalog.presentation.filter.CatalogFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CatalogFilter()).addPathPatterns("/shop/**");
    }

}
