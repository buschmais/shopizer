package com.salesmanager.core.business.integration.catalog.listener;

import com.salesmanager.catalog.api.dto.product.ProductDTO;
import com.salesmanager.common.model.integration.CreatedEvent;
import com.salesmanager.common.model.integration.DeletedEvent;
import com.salesmanager.common.model.integration.UpdatedEvent;
import com.salesmanager.common.business.exception.ServiceException;
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
    public void handleProductCreateEvent(CreatedEvent<ProductDTO> event) throws ServiceException {
        ProductDTO productDTO = event.getDto();
        if (productDTO != null) {
            createOrUpdateProductInfo(productDTO);
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
    public void handleProductUpdateEvent(UpdatedEvent<ProductDTO> event) throws ServiceException {
        ProductDTO productDTO = event.getDto();
        if (productDTO != null) {
            createOrUpdateProductInfo(productDTO);
        }
    }

    private void createOrUpdateProductInfo(ProductDTO productDTO) throws ServiceException {
        ProductInfo productInfo = new ProductInfo(
                productDTO.getId(),
                productDTO.getSku(),
                productDTO.getName(),
                productDTO.getManufacturerCode());
        ProductInfo.Dimension dimension = this.productInfoService.enrichDimensionsForProduct(productDTO.getId());
        productInfo.setDimension(dimension);
        ProductInfo.AvailabilityInformation availability = this.productInfoService.enrichAvailabilityInfoForProduct(productDTO.getId());
        productInfo.setAvailabilityInformation(availability);
        productInfo.setTaxClass(this.productInfoService.enrichTaxClassForProduct(productDTO.getId()));
        productInfo.setMerchantStore(this.productInfoService.enrichMerchantForProduct(productDTO.getId()));
        this.productInfoService.save(productInfo);
    }

}
