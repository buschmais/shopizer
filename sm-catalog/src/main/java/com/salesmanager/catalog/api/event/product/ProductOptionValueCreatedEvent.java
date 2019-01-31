package com.salesmanager.catalog.api.event.product;

import com.salesmanager.catalog.api.dto.product.ProductOptionValueDTO;
import com.salesmanager.common.model.integration.CreatedEvent;

public class ProductOptionValueCreatedEvent extends CreatedEvent<ProductOptionValueDTO> {

    public ProductOptionValueCreatedEvent(ProductOptionValueDTO dto) {
        super(dto);
    }

}
