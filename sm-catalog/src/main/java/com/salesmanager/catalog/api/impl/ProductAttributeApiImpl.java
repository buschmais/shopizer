package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.ProductAttributeApi;
import com.salesmanager.catalog.business.service.product.attribute.ProductAttributeService;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductAttributeApiImpl implements ProductAttributeApi {

    private ProductAttributeService productAttributeService;

    @Autowired
    public ProductAttributeApiImpl(ProductAttributeService productAttributeService) {
        this.productAttributeService = productAttributeService;
    }


    @Override
    public ProductAttribute getById(Long id) {
        return productAttributeService.getById(id);
    }
}
