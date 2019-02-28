package com.salesmanager.core.business.services.catalog;

import com.salesmanager.core.business.repositories.catalog.ProductOptionValueInfoRepository;
import com.salesmanager.core.model.catalog.ProductOptionValueInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductOptionValueInfoService {

    private final ProductOptionValueInfoRepository productOptionValueInfoRepository;

    @Autowired
    public ProductOptionValueInfoService(ProductOptionValueInfoRepository productOptionValueInfoRepository) {
        this.productOptionValueInfoRepository = productOptionValueInfoRepository;
    }

    public ProductOptionValueInfo save(ProductOptionValueInfo productOptionValueInfo) {
        return this.productOptionValueInfoRepository.save(productOptionValueInfo);
    }

    public void delete(Long productOptionValueId) {
        this.productOptionValueInfoRepository.delete(productOptionValueId);
    }

    public ProductOptionValueInfo findOne(Long productOptionValueId) {
        return this.productOptionValueInfoRepository.findOne(productOptionValueId);
    }
}
