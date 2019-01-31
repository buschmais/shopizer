package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.ProductRelationshipApi;
import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.business.service.product.relationship.ProductRelationshipService;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.model.product.relationship.ProductRelationship;
import com.salesmanager.catalog.model.product.relationship.ProductRelationshipType;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.integration.language.LanguageDTO;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductRelationshipApiImpl implements ProductRelationshipApi {

    private MerchantStoreInfoService merchantStoreInfoService;

    private ProductRelationshipService productRelationshipService;

    private LanguageInfoService languageInfoService;

    @Autowired
    public ProductRelationshipApiImpl(MerchantStoreInfoService merchantStoreInfoService, ProductRelationshipService productRelationshipService, LanguageInfoService languageInfoService) {
        this.merchantStoreInfoService = merchantStoreInfoService;
        this.productRelationshipService = productRelationshipService;
        this.languageInfoService = languageInfoService;
    }


    @Override
    public List<String> getGroups(MerchantStoreDTO store) {
        MerchantStoreInfo storeInfo = this.merchantStoreInfoService.findbyCode(store.getCode());
        List<ProductRelationship> groups = productRelationshipService.getGroups(storeInfo);
        if (groups != null) {
            return groups.stream().map(ProductRelationship::getCode).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<ProductRelationship> getByType(MerchantStoreDTO store, ProductRelationshipType type, LanguageDTO language) throws ServiceException {
        MerchantStoreInfo storeInfo = this.merchantStoreInfoService.findbyCode(store.getCode());
        LanguageInfo languageInfo = this.languageInfoService.findbyCode(language.getCode());
        return this.productRelationshipService.getByType(storeInfo,type, languageInfo);
    }
}
