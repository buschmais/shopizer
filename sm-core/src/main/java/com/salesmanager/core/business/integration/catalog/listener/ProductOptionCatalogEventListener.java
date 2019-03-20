package com.salesmanager.core.business.integration.catalog.listener;

import com.salesmanager.catalog.api.dto.product.ProductOptionDTO;
import com.salesmanager.core.business.integration.catalog.adapter.ProductOptionInfoAdapter;
import com.salesmanager.core.business.services.catalog.ProductOptionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductOptionCatalogEventListener {

    private final ProductOptionInfoService productOptionInfoService;
    private final ProductOptionInfoAdapter productOptionInfoAdapter;

    @Autowired
    public ProductOptionCatalogEventListener(ProductOptionInfoService productOptionInfoService, ProductOptionInfoAdapter productOptionInfoAdapter) {
        this.productOptionInfoService = productOptionInfoService;
        this.productOptionInfoAdapter = productOptionInfoAdapter;
    }

    @KafkaListener(topics = "product_option", containerFactory = "productOptionKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductOptionEvent(ProductOptionDTO productOptionDTO) {
        if (productOptionDTO != null) {
            switch (productOptionDTO.getEventType()) {
                case CREATE:
                case UPDATE:
                    this.productOptionInfoAdapter.createOrUpdateProductOptionInfo(productOptionDTO);
                    break;
                case DELETE:
                    this.productOptionInfoService.delete(productOptionDTO.getId());
                    break;
            }
        }
    }

}
