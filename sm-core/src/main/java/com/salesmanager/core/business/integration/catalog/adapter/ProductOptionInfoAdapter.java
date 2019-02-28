package com.salesmanager.core.business.integration.catalog.adapter;

import com.salesmanager.catalog.api.dto.product.ProductOptionDTO;
import com.salesmanager.core.business.services.catalog.ProductOptionInfoService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.ProductOptionDescriptionInfo;
import com.salesmanager.core.model.catalog.ProductOptionInfo;
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
public class ProductOptionInfoAdapter {

    private final LanguageService languageService;
    private final ProductOptionInfoService productOptionInfoService;
    private final RestTemplate catalogRestTemplate;


    @Autowired
    public ProductOptionInfoAdapter(LanguageService languageService, ProductOptionInfoService productOptionInfoService, RestTemplate catalogRestTemplate) {
        this.languageService = languageService;
        this.productOptionInfoService = productOptionInfoService;
        this.catalogRestTemplate = catalogRestTemplate;
    }

    public ProductOptionInfo findOrRequest(Long productOptionId) {
        ProductOptionInfo productOptionInfo = this.productOptionInfoService.findOne(productOptionId);
        if (productOptionInfo == null) {
            Map<String, Object> params = new HashMap<>();
            params.put("optionId", productOptionId);
            ResponseEntity<ProductOptionDTO> response = catalogRestTemplate.exchange("/catalog/product-option/{optionId}",
                    HttpMethod.GET, null, new ParameterizedTypeReference<ProductOptionDTO>() {}, params);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                productOptionInfo =  createOrUpdateProductOptionInfo(response.getBody());
            }
        }
        return productOptionInfo;
    }

    public ProductOptionInfo createOrUpdateProductOptionInfo(ProductOptionDTO productOptionDTO) {
        Set<ProductOptionDescriptionInfo> descriptions = new HashSet<>();
        if (productOptionDTO.getDescriptions() != null) {
            for (ProductOptionDTO.ProductOptionDescriptionDTO productOptionDescriptionDTO : productOptionDTO.getDescriptions()) {
                Language language = languageService.getById(productOptionDescriptionDTO.getLanguageId());
                descriptions.add(new ProductOptionDescriptionInfo(productOptionDescriptionDTO.getId(), productOptionDescriptionDTO.getName(), language));
            }
        }
        return this.productOptionInfoService.save(new ProductOptionInfo(productOptionDTO.getId(), productOptionDTO.getCode(), descriptions));
    }

}
