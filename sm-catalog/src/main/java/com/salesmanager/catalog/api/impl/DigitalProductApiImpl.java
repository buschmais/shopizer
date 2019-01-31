package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.DigitalProductApi;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.business.service.product.ProductService;
import com.salesmanager.catalog.business.service.product.file.DigitalProductService;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.file.DigitalProduct;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DigitalProductApiImpl implements DigitalProductApi {

    private DigitalProductService digitalProductService;

    private MerchantStoreInfoService merchantStoreInfoService;

    private ProductService productService;

    @Autowired
    public DigitalProductApiImpl(DigitalProductService digitalProductService, MerchantStoreInfoService merchantStoreInfoService, ProductService productService) {
        this.digitalProductService = digitalProductService;
        this.merchantStoreInfoService = merchantStoreInfoService;
        this.productService = productService;
    }

    @Override
    public String getFileNameByProduct(MerchantStoreDTO store, Long productId) throws ServiceException {
        MerchantStoreInfo storeInfo = this.merchantStoreInfoService.findbyCode(store.getCode());
        Product product = this.productService.getById(productId);
        DigitalProduct digitalProduct = digitalProductService.getByProduct(storeInfo, product);
        return digitalProduct != null ? digitalProduct.getProductFileName() : null;
    }

}
