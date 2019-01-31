package com.salesmanager.core.business.services.catalog;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.dto.product.DimensionDTO;
import com.salesmanager.core.business.repositories.catalog.ProductInfoRepository;
import com.salesmanager.core.model.catalog.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProductInfoService {

    private final ProductApi productApi;

    private final ProductInfoRepository productInfoRepository;

    @Autowired
    public ProductInfoService(ProductApi productApi, ProductInfoRepository productInfoRepository) {
        this.productApi = productApi;
        this.productInfoRepository = productInfoRepository;
    }

    public ProductInfo save(ProductInfo productInfo) {
        return this.productInfoRepository.save(productInfo);
    }

    public void delete(Long productId) {
        this.productInfoRepository.delete(productId);
    }

    public ProductInfo.Dimension enrichDimensionsForProduct(Long productId) {
        DimensionDTO dimensionDTO = productApi.getProductDimension(productId);
        if (dimensionDTO != null) {
            return new ProductInfo.Dimension(dimensionDTO.getWidth(), dimensionDTO.getLength(), dimensionDTO.getHeight(), dimensionDTO.getWeight());
        }
        return null;
    }
}
