package com.salesmanager.core.business.integration.catalog.listener;

import com.salesmanager.catalog.api.dto.product.ProductOptionValueDTO;import com.salesmanager.core.business.integration.catalog.adapter.ProductOptionValueInfoAdapter;
import com.salesmanager.core.business.services.catalog.ProductOptionValueInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductOptionValueCatalogEventListener {

    private final ProductOptionValueInfoAdapter productOptionValueInfoAdapter;
    private final ProductOptionValueInfoService productOptionValueInfoService;

    @Autowired
    public ProductOptionValueCatalogEventListener(ProductOptionValueInfoAdapter productOptionValueInfoAdapter, ProductOptionValueInfoService productOptionValueInfoService) {
        this.productOptionValueInfoAdapter = productOptionValueInfoAdapter;
        this.productOptionValueInfoService = productOptionValueInfoService;
    }

    @KafkaListener(topics = "product_option_value", containerFactory = "productOptionValueKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductOptionValueEvent(ProductOptionValueDTO productOptionValueDTO) {
        if (productOptionValueDTO != null) {
            switch (productOptionValueDTO.getEventType()) {
                case CREATED:
                case UPDATED:
                    this.productOptionValueInfoAdapter.createOrUpdateProductOptionValueInfo(productOptionValueDTO);
                    break;
                case DELETED:
                    this.productOptionValueInfoService.delete(productOptionValueDTO.getId());
                    break;
            }

        }
    }

}
