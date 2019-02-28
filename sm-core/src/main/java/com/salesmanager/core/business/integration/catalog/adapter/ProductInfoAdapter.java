package com.salesmanager.core.business.integration.catalog.adapter;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.dto.product.ProductAttributeDTO;
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
    private final ProductOptionValueInfoAdapter productOptionValueInfoAdapter;
    private final ProductApi productApi;

    @Autowired
    public ProductInfoAdapter(ProductOptionInfoAdapter productOptionInfoAdapter, ProductOptionValueInfoAdapter productOptionValueInfoAdapter, ProductApi productApi) {
        this.productOptionInfoAdapter = productOptionInfoAdapter;
        this.productOptionValueInfoAdapter = productOptionValueInfoAdapter;
        this.productApi = productApi;
    }

    public Set<ProductAttributeInfo> enrichProductAttributesForProduct(Long productId) {
        Set<ProductAttributeDTO> productAttributeDTOs = productApi.getProductAttributes(productId);
        Set<ProductAttributeInfo> productAttributeInfos = new HashSet<>();
        if (productAttributeDTOs != null) {
            for (ProductAttributeDTO dto : productAttributeDTOs) {
                ProductOptionInfo optionInfo = productOptionInfoAdapter.findOrRequest(dto.getProductOptionId());
                ProductOptionValueInfo valueInfo = productOptionValueInfoAdapter.findOrRequest(dto.getProductOptionValueId());
                productAttributeInfos.add(new ProductAttributeInfo(dto.getId(), dto.getPrice(), dto.getFree(), dto.getWeight(), optionInfo, valueInfo));
            }
        }
        return productAttributeInfos;
    }

}
