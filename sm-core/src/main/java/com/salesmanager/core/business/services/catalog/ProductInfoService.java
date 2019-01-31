package com.salesmanager.core.business.services.catalog;

import com.salesmanager.core.business.repositories.catalog.ProductInfoRepository;
import com.salesmanager.core.model.catalog.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductInfoService {

    private final ProductInfoRepository productInfoRepository;

    @Autowired
    public ProductInfoService(ProductInfoRepository productInfoRepository) {
        this.productInfoRepository = productInfoRepository;
    }

    public ProductInfo save(ProductInfo productInfo) {
        return this.productInfoRepository.save(productInfo);
    }

    public void delete(Long productId) {
        this.productInfoRepository.delete(productId);
    }
}
