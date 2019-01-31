package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.ProductRelationshipApi;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.business.service.product.relationship.ProductRelationshipService;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.model.product.relationship.ProductRelationship;
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

    @Autowired
    public ProductRelationshipApiImpl(MerchantStoreInfoService merchantStoreInfoService, ProductRelationshipService productRelationshipService) {
        this.merchantStoreInfoService = merchantStoreInfoService;
        this.productRelationshipService = productRelationshipService;
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

}
