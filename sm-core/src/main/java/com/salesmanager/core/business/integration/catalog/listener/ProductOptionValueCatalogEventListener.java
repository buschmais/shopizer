package com.salesmanager.core.business.integration.catalog.listener;

import com.salesmanager.catalog.api.dto.product.ProductOptionValueDTO;
import com.salesmanager.core.business.services.catalog.ProductOptionValueInfoService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.ProductOptionValueDescriptionInfo;
import com.salesmanager.core.model.catalog.ProductOptionValueInfo;
import com.salesmanager.core.model.reference.language.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class ProductOptionValueCatalogEventListener {

    private final LanguageService languageService;

    private final ProductOptionValueInfoService productOptionValueInfoService;

    @Autowired
    public ProductOptionValueCatalogEventListener(LanguageService languageService, ProductOptionValueInfoService productOptionValueInfoService) {
        this.languageService = languageService;
        this.productOptionValueInfoService = productOptionValueInfoService;
    }

    @KafkaListener(topics = "product_option_value", containerFactory = "productOptionValueKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductOptionValueEvent(ProductOptionValueDTO productOptionValueDTO) {
        if (productOptionValueDTO != null) {
            switch (productOptionValueDTO.getEventType()) {
                case CREATED:
                case UPDATED:
                    ProductOptionValueInfo productOptionValueInfo = createProductOptionValueInfo(productOptionValueDTO);
                    this.productOptionValueInfoService.save(productOptionValueInfo);
                    break;
                case DELETED:
                    this.productOptionValueInfoService.delete(productOptionValueDTO.getId());
                    break;
            }

        }
    }

    private ProductOptionValueInfo createProductOptionValueInfo(ProductOptionValueDTO productOptionValueDTO) {
        Set<ProductOptionValueDescriptionInfo> descriptions = new HashSet<>();
        if (productOptionValueDTO.getDescriptions() != null) {
            for (ProductOptionValueDTO.ProductOptionValueDescriptionDTO productOptionValueDescriptionDTO : productOptionValueDTO.getDescriptions()) {
                Language language = languageService.getById(productOptionValueDescriptionDTO.getLanguageId());
                descriptions.add(new ProductOptionValueDescriptionInfo(productOptionValueDescriptionDTO.getId(), productOptionValueDescriptionDTO.getName(), language));
            }
        }
        return new ProductOptionValueInfo(productOptionValueDTO.getId(), productOptionValueDTO.getCode(),  descriptions);
    }

}
