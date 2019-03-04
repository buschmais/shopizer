package com.salesmanager.core.business.integration.catalog.adapter;

import com.salesmanager.core.business.integration.catalog.dto.AvailabilityInformationDTO;
import com.salesmanager.core.business.integration.catalog.dto.DimensionDTO;
import com.salesmanager.core.business.integration.catalog.dto.ProductAttributeDTO;
import com.salesmanager.core.business.integration.catalog.dto.ProductDescriptionDTO;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.model.catalog.*;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
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
    private final MerchantStoreService merchantStoreService;
    private final TaxClassService taxClassService;
    private final RestTemplate catalogRestTemplate;

    @Autowired
    public ProductInfoAdapter(ProductOptionInfoAdapter productOptionInfoAdapter, ProductOptionValueInfoAdapter productOptionValueInfoAdapter, MerchantStoreService merchantStoreService, TaxClassService taxClassService, RestTemplate catalogRestTemplate) {
        this.productOptionInfoAdapter = productOptionInfoAdapter;
        this.productOptionValueInfoAdapter = productOptionValueInfoAdapter;
        this.merchantStoreService = merchantStoreService;
        this.taxClassService = taxClassService;
        this.catalogRestTemplate = catalogRestTemplate;
    }

    public Set<ProductAttributeInfo> requestProductAttributesForProduct(Long productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        ResponseEntity<Set<ProductAttributeDTO>> response = catalogRestTemplate.exchange("/catalog/product/{productId}/attributes",
                HttpMethod.GET, null, new ParameterizedTypeReference<Set<ProductAttributeDTO>>() {}, params);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Set<ProductAttributeDTO> productAttributeDTOs = response.getBody();
            Set<ProductAttributeInfo> productAttributeInfos = new HashSet<>();
            if (productAttributeDTOs != null) {
                for (ProductAttributeDTO dto : productAttributeDTOs) {
                    ProductOptionInfo optionInfo = productOptionInfoAdapter.findOrRequest(dto.getProductOptionId());
                    ProductOptionValueInfo valueInfo = productOptionValueInfoAdapter.findOrRequest(dto.getProductOptionValueId());
                    productAttributeInfos.add(new ProductAttributeInfo(dto.getId(), dto.getPrice(), dto.getFree(), dto.getWeight(), optionInfo, valueInfo));
                }
                return productAttributeInfos;
            }
        }
        return null;
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

    public MerchantStore requestMerchantForProduct(Long productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        ResponseEntity<Integer> response = catalogRestTemplate.exchange("/catalog/product/{productId}/merchant",
                HttpMethod.GET, null, new ParameterizedTypeReference<Integer>() {}, params);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Integer merchantStoreId = response.getBody();
            if (merchantStoreId != null) {
                return this.merchantStoreService.getById(merchantStoreId);
            }
        }
        return null;
    }

    public TaxClass requestTaxClassForProduct(Long productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        ResponseEntity<Long> response = catalogRestTemplate.exchange("/catalog/product/{productId}/tax-class",
                HttpMethod.GET, null, new ParameterizedTypeReference<Long>() {}, params);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Long taxClassId = response.getBody();
            if (taxClassId != null) {
                return this.taxClassService.getById(taxClassId);
            }
        }
        return null;
    }

}
