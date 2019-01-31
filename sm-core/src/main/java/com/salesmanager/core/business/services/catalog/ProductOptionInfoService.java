package com.salesmanager.core.business.services.catalog;

import com.salesmanager.core.business.repositories.catalog.ProductOptionInfoRepository;
import com.salesmanager.core.model.catalog.ProductOptionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductOptionInfoService {

    private final ProductOptionInfoRepository productOptionRepository;

    @Autowired
    public ProductOptionInfoService(ProductOptionInfoRepository productOptionRepository) {
        this.productOptionRepository = productOptionRepository;
    }

    public ProductOptionInfo save(ProductOptionInfo productOptionInfo) {
        return this.productOptionRepository.save(productOptionInfo);
    }

    public void delete(Long productOptionId) {
        this.productOptionRepository.delete(productOptionId);
    }
}
