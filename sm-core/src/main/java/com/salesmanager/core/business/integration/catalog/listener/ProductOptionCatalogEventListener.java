package com.salesmanager.core.business.integration.catalog.listener;

import com.salesmanager.catalog.api.dto.product.ProductOptionDTO;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.ProductOptionInfoService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.ProductOptionDescriptionInfo;
import com.salesmanager.core.model.catalog.ProductOptionInfo;
import com.salesmanager.core.model.reference.language.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class ProductOptionCatalogEventListener {

    private final LanguageService languageService;

    private final ProductOptionInfoService productOptionInfoService;

    @Autowired
    public ProductOptionCatalogEventListener(LanguageService languageService, ProductOptionInfoService productOptionInfoService) {
        this.languageService = languageService;
        this.productOptionInfoService = productOptionInfoService;
    }

    @KafkaListener(topics = "product_option", containerFactory = "productOptionKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductOptionEvent(ProductOptionDTO productOptionDTO) {
        if (productOptionDTO != null) {
            switch (productOptionDTO.getEventType()) {
                case CREATED:
                case UPDATED:
                    ProductOptionInfo productOption = createProductOptionInfo(productOptionDTO);
                    this.productOptionInfoService.save(productOption);
                    break;
                case DELETED:
                    this.productOptionInfoService.delete(productOptionDTO.getId());
                    break;
            }
        }
    }

    private ProductOptionInfo createProductOptionInfo(ProductOptionDTO productOptionDTO) {
        Set<ProductOptionDescriptionInfo> descriptions = new HashSet<>();
        if (productOptionDTO.getDescriptions() != null) {
            for (ProductOptionDTO.ProductOptionDescriptionDTO productOptionDescriptionDTO : productOptionDTO.getDescriptions()) {
                Language language = languageService.getById(productOptionDescriptionDTO.getLanguageId());
                descriptions.add(new ProductOptionDescriptionInfo(productOptionDescriptionDTO.getId(), productOptionDescriptionDTO.getName(), language));
            }
        }
        return new ProductOptionInfo(productOptionDTO.getId(), productOptionDTO.getCode(), descriptions);
    }

}
