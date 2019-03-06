package com.salesmanager.core.business.integration.catalog.adapter;

import com.salesmanager.core.business.integration.catalog.dto.ProductOptionValueDTO;
import com.salesmanager.core.business.services.catalog.ProductOptionValueInfoService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.ProductOptionValueDescriptionInfo;
import com.salesmanager.core.model.catalog.ProductOptionValueInfo;
import com.salesmanager.core.model.reference.language.Language;
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
public class ProductOptionValueInfoAdapter {

    private final LanguageService languageService;
    private final ProductOptionValueInfoService productOptionValueInfoService;
    private final RestTemplate catalogRestTemplate;

    @Autowired
    public ProductOptionValueInfoAdapter(LanguageService languageService, ProductOptionValueInfoService productOptionValueInfoService, RestTemplate catalogRestTemplate) {
        this.languageService = languageService;
        this.productOptionValueInfoService = productOptionValueInfoService;
        this.catalogRestTemplate = catalogRestTemplate;
    }

    public ProductOptionValueInfo findOrRequest(Long productOptionValueId) {
        ProductOptionValueInfo productOptionValueInfo = this.productOptionValueInfoService.findOne(productOptionValueId);
        if (productOptionValueInfo == null) {
            Map<String, Object> params = new HashMap<>();
            params.put("valueId", productOptionValueId);
            ResponseEntity<ProductOptionValueDTO> response = catalogRestTemplate.exchange("/catalog/product-option-value/{valueId}",
                    HttpMethod.GET, null, new ParameterizedTypeReference<ProductOptionValueDTO>() {}, params);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                productOptionValueInfo =  createOrUpdateProductOptionValueInfo(response.getBody());
            }
        }
        return productOptionValueInfo;
    }

    public ProductOptionValueInfo createOrUpdateProductOptionValueInfo(ProductOptionValueDTO productOptionValueDTO) {
        Set<ProductOptionValueDescriptionInfo> descriptions = new HashSet<>();
        if (productOptionValueDTO.getDescriptions() != null) {
            for (ProductOptionValueDTO.ProductOptionValueDescriptionDTO productOptionValueDescriptionDTO : productOptionValueDTO.getDescriptions()) {
                Language language = languageService.getById(productOptionValueDescriptionDTO.getLanguageId());
                descriptions.add(new ProductOptionValueDescriptionInfo(productOptionValueDescriptionDTO.getId(), productOptionValueDescriptionDTO.getName(), language));
            }
        }
        return this.productOptionValueInfoService.save(new ProductOptionValueInfo(productOptionValueDTO.getId(), productOptionValueDTO.getCode(),  descriptions));
    }
}
