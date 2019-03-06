package com.salesmanager.core.business.integration.catalog.adapter;

import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class CategoryInfoAdapter {

    private final RestTemplate catalogRestTemplate;

    @Autowired
    public CategoryInfoAdapter(RestTemplate catalogRestTemplate) {
        this.catalogRestTemplate = catalogRestTemplate;
    }

    public BreadcrumbItem getCategoryBreadcrumbItem(Long categoryId, String languageCode) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/catalog/rest/category/{categoryId}/breadcrumb");
        uriBuilder.queryParam("languageCode", languageCode);
        uriBuilder.buildAndExpand(params);
        ResponseEntity<BreadcrumbItem> breadcrumbResponse = catalogRestTemplate.exchange(uriBuilder.buildAndExpand(params).toString(), HttpMethod.GET, null, BreadcrumbItem.class);
        if (breadcrumbResponse.getStatusCode().is2xxSuccessful()) {
            return breadcrumbResponse.getBody();
        } else {
            throw new ServiceException("Category breadcrumb item not accessible from service, status code: " + breadcrumbResponse.getStatusCode());
        }
    }
}
