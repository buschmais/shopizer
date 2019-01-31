package com.salesmanager.core.business.integration.catalog.listener;

import com.salesmanager.catalog.api.dto.product.ProductDTO;
import com.salesmanager.common.model.integration.CreatedEvent;
import com.salesmanager.common.model.integration.DeletedEvent;
import com.salesmanager.common.model.integration.UpdatedEvent;
import com.salesmanager.core.business.services.catalog.ProductInfoService;
import com.salesmanager.core.model.catalog.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ProductCatalogEventListener {

    private final ProductInfoService productInfoService;

    @Autowired
    public ProductCatalogEventListener(ProductInfoService productInfoService) {
        this.productInfoService = productInfoService;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductCreateEvent(CreatedEvent<ProductDTO> event) {
        ProductDTO productDTO = event.getDto();
        if (productDTO != null) {
            ProductInfo productInfo = new ProductInfo(
                    productDTO.getId(),
                    productDTO.getSku(),
                    productDTO.getName(),
                    productDTO.getManufacturerCode());
            this.productInfoService.save(productInfo);
        }
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductDeleteEvent(DeletedEvent<ProductDTO> event) {
        ProductDTO productDTO = event.getDto();
        if (productDTO != null) {
            this.productInfoService.delete(productDTO.getId());
        }
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductUpdateEvent(UpdatedEvent<ProductDTO> event) {
        ProductDTO productDTO = event.getDto();
        if (productDTO != null) {
            ProductInfo productInfo = new ProductInfo(
                    productDTO.getId(),
                    productDTO.getSku(),
                    productDTO.getName(),
                    productDTO.getManufacturerCode());
            this.productInfoService.save(productInfo);
        }
    }

}
