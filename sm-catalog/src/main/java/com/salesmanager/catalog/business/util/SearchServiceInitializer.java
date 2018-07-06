package com.salesmanager.catalog.business.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;

import com.salesmanager.catalog.business.service.search.SearchService;
import org.springframework.stereotype.Component;

@Component
public class SearchServiceInitializer implements ApplicationListener<ContextStartedEvent> {

	@Override
	public void onApplicationEvent(ContextStartedEvent event) {
		 ApplicationContext applicationContext = event.getApplicationContext();
		 /** init search service **/
		 SearchService searchService = (SearchService)applicationContext.getBean("productSearchService");
		 searchService.initService();
	}

}
