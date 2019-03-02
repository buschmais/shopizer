package com.salesmanager.core.business.integration.catalog.listener;

import com.salesmanager.catalog.api.dto.product.ProductDTO;
import com.salesmanager.core.business.integration.catalog.adapter.ProductInfoAdapter;
import com.salesmanager.core.business.services.catalog.ProductInfoService;
import com.salesmanager.core.model.catalog.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductCatalogEventListener {

    private final ProductInfoAdapter productInfoAdapter;
    private final ProductInfoService productInfoService;

    @Autowired
    public ProductCatalogEventListener(ProductInfoAdapter productInfoAdapter, ProductInfoService productInfoService) {
        this.productInfoAdapter = productInfoAdapter;
        this.productInfoService = productInfoService;
    }

    @KafkaListener(topics = "product", containerFactory = "productKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProductEvent(ProductDTO productDTO) {
        if (productDTO != null) {
            switch (productDTO.getEventType()) {
                case CREATED:
                case UPDATED:
                    createOrUpdateProductInfo(productDTO);
                    break;
                case DELETED:
                    this.productInfoService.delete(productDTO.getId());
                    break;
            }
        }
    }

    private void createOrUpdateProductInfo(ProductDTO productDTO) {
        ProductInfo productInfo = new ProductInfo(
                productDTO.getId(),
                productDTO.getSku(),
                productDTO.getName(),
                productDTO.getManufacturerCode());
        ProductInfo.Dimension dimension = this.productInfoAdapter.requestDimensionsForProduct(productDTO.getId());
        productInfo.setDimension(dimension);
        ProductInfo.AvailabilityInformation availability = this.productInfoService.enrichAvailabilityInfoForProduct(productDTO.getId());
        productInfo.setAvailabilityInformation(availability);
        productInfo.setTaxClass(this.productInfoService.enrichTaxClassForProduct(productDTO.getId()));
        productInfo.setMerchantStore(this.productInfoService.enrichMerchantForProduct(productDTO.getId()));
        productInfo.setAttributes(this.productInfoAdapter.enrichProductAttributesForProduct(productDTO.getId()));
        productInfo.setDescriptions(this.productInfoService.enrichProductDescriptionsForProduct(productDTO.getId()));
        this.productInfoService.save(productInfo);
    }

}
