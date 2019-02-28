package com.salesmanager.core.business.integration.catalog.adapter;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.dto.product.ProductAttributeDTO;
import com.salesmanager.core.business.repositories.catalog.ProductOptionValueInfoRepository;
import com.salesmanager.core.model.catalog.ProductAttributeInfo;
import com.salesmanager.core.model.catalog.ProductOptionInfo;
import com.salesmanager.core.model.catalog.ProductOptionValueInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ProductInfoAdapter {

    private final ProductOptionInfoAdapter productOptionInfoAdapter;
    private final ProductOptionValueInfoRepository productOptionValueInfoRepository;
    private final ProductApi productApi;

    @Autowired
    public ProductInfoAdapter(ProductOptionInfoAdapter productOptionInfoAdapter, ProductOptionValueInfoRepository productOptionValueInfoRepository, ProductApi productApi) {
        this.productOptionInfoAdapter = productOptionInfoAdapter;
        this.productOptionValueInfoRepository = productOptionValueInfoRepository;
        this.productApi = productApi;
    }

    public Set<ProductAttributeInfo> enrichProductAttributesForProduct(Long productId) {
        Set<ProductAttributeDTO> productAttributeDTOs = productApi.getProductAttributes(productId);
        Set<ProductAttributeInfo> productAttributeInfos = new HashSet<>();
        if (productAttributeDTOs != null) {
            for (ProductAttributeDTO dto : productAttributeDTOs) {
                ProductOptionInfo optionInfo = productOptionInfoAdapter.findOrRequest(dto.getProductOptionId());
                ProductOptionValueInfo valueInfo = productOptionValueInfoRepository.findOne(dto.getProductOptionValueId());
                productAttributeInfos.add(new ProductAttributeInfo(dto.getId(), dto.getPrice(), dto.getFree(), dto.getWeight(), optionInfo, valueInfo));
            }
        }
        return productAttributeInfos;
    }

}
