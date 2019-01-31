package com.salesmanager.core.business.integration.catalog.listener;

import com.salesmanager.catalog.api.dto.product.ProductOptionValueDTO;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.model.integration.CreatedEvent;
import com.salesmanager.common.model.integration.DeletedEvent;
import com.salesmanager.common.model.integration.UpdatedEvent;
import com.salesmanager.core.business.services.catalog.ProductOptionValueInfoService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.ProductOptionValueDescriptionInfo;
import com.salesmanager.core.model.catalog.ProductOptionValueInfo;
import com.salesmanager.core.model.reference.language.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

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

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductOptionValueCreateEvent(CreatedEvent<ProductOptionValueDTO> event) throws ServiceException {
        ProductOptionValueDTO productOptionValueDTO = event.getDto();
        if (productOptionValueDTO != null) {
            ProductOptionValueInfo productOptionValueInfo = createProductOptionValueInfo(productOptionValueDTO);
            this.productOptionValueInfoService.save(productOptionValueInfo);
        }
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductOptionValueDeleteEvent(DeletedEvent<ProductOptionValueDTO> event) {
        ProductOptionValueDTO productOptionValueDTO = event.getDto();
        if (productOptionValueDTO != null) {
            this.productOptionValueInfoService.delete(productOptionValueDTO.getId());
        }
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductOptionValueUpdateEvent(UpdatedEvent<ProductOptionValueDTO> event) throws ServiceException {
        ProductOptionValueDTO productOptionValueDTO = event.getDto();
        if (productOptionValueDTO != null) {
            ProductOptionValueInfo productOptionValueInfo = createProductOptionValueInfo(productOptionValueDTO);
            this.productOptionValueInfoService.save(productOptionValueInfo);
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
