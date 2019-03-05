package com.salesmanager.core.business.integration.catalog.adapter;

import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.core.business.integration.catalog.dto.*;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.model.catalog.*;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

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

    public ProductImageInfo requestProductImage(Long productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        ResponseEntity<ProductImageDTO> response = catalogRestTemplate.exchange("/catalog/product/{productId}/default-image",
                HttpMethod.GET, null, new ParameterizedTypeReference<ProductImageDTO>() {}, params);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            ProductImageDTO productImageDTO = response.getBody();
            return new ProductImageInfo(productImageDTO.getId(), productImageDTO.getImageName(), productImageDTO.isDefaultImage(), productImageDTO.getImageUrl());
        }
        return null;
    }

    public FinalPriceInfo requestProductFinalPrice(Long productId, List<ProductAttributeInfo> productAttributes) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        if (productAttributes != null && !productAttributes.isEmpty()) {
            params.put("attributeIds", productAttributes.stream().map(ProductAttributeInfo::getId).collect(Collectors.toList()));
        }
        ResponseEntity<FinalPriceDTO> priceResponse = catalogRestTemplate.exchange("/catalog/product/{productId}/final-price",
                HttpMethod.GET, null, new ParameterizedTypeReference<FinalPriceDTO>() {}, params);
        if (priceResponse.getStatusCode().is2xxSuccessful() && priceResponse.getBody() != null) {
            FinalPriceDTO finalPriceDTO = priceResponse.getBody();
            return toInfo(finalPriceDTO);
        } else {
            throw new ServiceException("Product final price not accessible from service, status code: " + priceResponse.getStatusCode());
        }
    }

    private FinalPriceInfo toInfo(FinalPriceDTO finalPriceDTO) {
        List<FinalPriceInfo> additionalPrices = new ArrayList<>();
        if (finalPriceDTO.getAdditionalPrices() != null) {
            finalPriceDTO.getAdditionalPrices().stream().map(this::toInfo).collect(Collectors.toCollection(() -> additionalPrices));
        }
        ProductPriceInfo productPriceInfo = null;
        if (finalPriceDTO.getProductPrice() != null) {
            ProductPriceDTO productPriceDTO = finalPriceDTO.getProductPrice();
            productPriceInfo = new ProductPriceInfo(
                    productPriceDTO.getCode(),
                    productPriceDTO.getProductPriceType(),
                    productPriceDTO.getDefaultPrice(),
                    productPriceDTO.getName(),
                    productPriceDTO.getProductPriceSpecialAmount(),
                    productPriceDTO.getProductPriceSpecialStartDate(),
                    productPriceDTO.getProductPriceSpecialEndDate()
            );
        }
        return new FinalPriceInfo(finalPriceDTO.getDiscounted(), finalPriceDTO.getFinalPrice(), finalPriceDTO.getDefaultPrice(), additionalPrices,productPriceInfo);
    }

    public Integer requestAvailbilityForRegion(Long productId, String region) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/catalog/product/{productId}/availability");
        uriBuilder.queryParam("region", region);
        uriBuilder.buildAndExpand(params);
        ResponseEntity<Integer> availabilityResponse = catalogRestTemplate.exchange(uriBuilder.buildAndExpand(params).toString(), HttpMethod.GET, null, Integer.class);
        if (availabilityResponse.getStatusCode().is2xxSuccessful()) {
            return availabilityResponse.getBody();
        } else {
            throw new ServiceException("Product availability not accessible from service, status code: " + availabilityResponse.getStatusCode());
        }
    }

    public boolean isAvailable(Long productId) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        ResponseEntity<Boolean> availabilityResponse = catalogRestTemplate.exchange("/catalog/product/{productId}/available", HttpMethod.GET, null, new ParameterizedTypeReference<Boolean>() {}, params);
        if (availabilityResponse.getStatusCode().is2xxSuccessful()) {
            return availabilityResponse.getBody();
        } else {
            throw new ServiceException("Product availability not accessible from service, status code: " + availabilityResponse.getStatusCode());
        }
    }

    public String requestFileNameByProduct(String storeCode, Long productId) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/catalog/product/{productId}/digital/file-name");
        uriBuilder.queryParam("storeCode", storeCode);
        uriBuilder.buildAndExpand(params);
        ResponseEntity<String> digitalResponse = catalogRestTemplate.exchange(uriBuilder.buildAndExpand(params).toString(), HttpMethod.GET, null, String.class);
        if (digitalResponse.getStatusCode().is2xxSuccessful()) {
            return digitalResponse.getBody();
        } else {
            throw new ServiceException("Digital product name not accessible from service, status code: " + digitalResponse.getStatusCode());
        }
    }

    public List<String> requestProductGroups(String storeCode) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("storeCode", storeCode);
        ResponseEntity<List<String>> relationResponse = catalogRestTemplate.exchange("/catalog/product/store/{storeCode}/groups", HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {}, params);
        if (relationResponse.getStatusCode().is2xxSuccessful()) {
            return relationResponse.getBody();
        } else {
            throw new ServiceException("Product groups not send and not accessible from service, status code: " + relationResponse.getStatusCode());
        }
    }

    public BreadcrumbItem requestBreadcrumbItemForLocale(Long productId, Language language, Locale locale) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/catalog/product/{productId}/breadcrumb");
        uriBuilder.queryParam("languageCode", language.getCode());
        uriBuilder.queryParam("locale", locale);
        uriBuilder.buildAndExpand(params);
        ResponseEntity<BreadcrumbItem> breadcrumbResponse = catalogRestTemplate.exchange(uriBuilder.buildAndExpand(params).toString(), HttpMethod.GET, null, BreadcrumbItem.class);
        if (breadcrumbResponse.getStatusCode().is2xxSuccessful()) {
            return breadcrumbResponse.getBody();
        } else {
            throw new ServiceException("Product breadcrumb item not accessible from service, status code: " + breadcrumbResponse.getStatusCode());
        }
    }
}
