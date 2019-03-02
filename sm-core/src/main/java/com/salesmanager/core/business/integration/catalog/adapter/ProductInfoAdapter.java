package com.salesmanager.core.business.integration.catalog.adapter;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.dto.product.ProductAttributeDTO;
import com.salesmanager.core.business.integration.catalog.dto.AvailabilityInformationDTO;
import com.salesmanager.core.business.integration.catalog.dto.DimensionDTO;
import com.salesmanager.core.business.integration.catalog.dto.ProductDescriptionDTO;
import com.salesmanager.core.model.catalog.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ProductInfoAdapter {

    private final ProductOptionInfoAdapter productOptionInfoAdapter;
    private final ProductOptionValueInfoAdapter productOptionValueInfoAdapter;
    private final ProductApi productApi;
    private final RestTemplate catalogRestTemplate;

    @Autowired
    public ProductInfoAdapter(ProductOptionInfoAdapter productOptionInfoAdapter, ProductOptionValueInfoAdapter productOptionValueInfoAdapter, ProductApi productApi, RestTemplate catalogRestTemplate) {
        this.productOptionInfoAdapter = productOptionInfoAdapter;
        this.productOptionValueInfoAdapter = productOptionValueInfoAdapter;
        this.productApi = productApi;
        this.catalogRestTemplate = catalogRestTemplate;
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

    public ProductInfo.Dimension requestDimensionsForProduct(Long productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        ResponseEntity<DimensionDTO> response = catalogRestTemplate.exchange("/catalog/product/{productId}/dimension",
                HttpMethod.GET, null, new ParameterizedTypeReference<DimensionDTO>() {}, params);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            DimensionDTO dimensionDTO = response.getBody();
            return new ProductInfo.Dimension(dimensionDTO.getWidth(), dimensionDTO.getLength(), dimensionDTO.getHeight(), dimensionDTO.getWeight());
        }
        return null;
    }

    public ProductInfo.AvailabilityInformation requestAvailabilityInfoForProduct(Long productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        ResponseEntity<AvailabilityInformationDTO> response = catalogRestTemplate.exchange("/catalog/product/{productId}/shipping",
                HttpMethod.GET, null, new ParameterizedTypeReference<AvailabilityInformationDTO>() {}, params);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            AvailabilityInformationDTO availabilityInformationDTO = response.getBody();
            return new ProductInfo.AvailabilityInformation(availabilityInformationDTO.isAvailable(), availabilityInformationDTO.isShippable(), availabilityInformationDTO.isVirtual());
        }
        return null;
    }

    public Set<ProductDescriptionInfo> requestProductDescriptionsForProduct(Long productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        ResponseEntity<Set<ProductDescriptionDTO>> response = catalogRestTemplate.exchange("/catalog/product/{productId}/descriptions",
                HttpMethod.GET, null, new ParameterizedTypeReference<Set<ProductDescriptionDTO>>() {}, params);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Set<ProductDescriptionDTO> productDescriptionDTOs = response.getBody();
            Set<ProductDescriptionInfo> productDescriptionInfos = new HashSet<>();
            for (ProductDescriptionDTO dto : productDescriptionDTOs) {
                productDescriptionInfos.add(new ProductDescriptionInfo(dto.getId(), dto.getName(), dto.getSeUrl(), dto.getLanguageId()));
            }
            return productDescriptionInfos;
        }
        return null;
    }

}
