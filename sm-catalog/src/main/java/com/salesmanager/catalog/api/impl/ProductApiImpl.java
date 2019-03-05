package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.service.product.ProductService;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.availability.ProductAvailability;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.common.presentation.model.BreadcrumbItemType;
import com.salesmanager.common.presentation.util.DateUtil;
import com.salesmanager.core.integration.language.LanguageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

@Component
public class ProductApiImpl implements ProductApi {

    private ProductService productService;

    private LanguageInfoService languageInfoService;

    @Autowired
    public ProductApiImpl(ProductService productService, LanguageInfoService languageInfoService) {
        this.productService = productService;
        this.languageInfoService = languageInfoService;
    }

    @Override
    public BreadcrumbItem getBreadcrumbItemForLocale(long productId, LanguageDTO languageDTO, Locale locale) throws ServiceException {
        LanguageInfo languageInfo = this.languageInfoService.findbyCode(languageDTO.getCode());
        Product product = this.productService.getProductForLocale(productId, languageInfo, locale);
        if(product!=null) {
            BreadcrumbItem productItem = new  BreadcrumbItem();
            productItem.setId(product.getId());
            productItem.setItemType(BreadcrumbItemType.PRODUCT);
            productItem.setLabel(product.getProductDescription().getName());
            productItem.setUrl(product.getProductDescription().getSeUrl());
            return productItem;
        }
        return null;
    }
}
