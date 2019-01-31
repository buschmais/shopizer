package com.salesmanager.catalog.api.event.product;

import com.salesmanager.catalog.api.dto.product.ProductDTO;
import com.salesmanager.common.model.integration.CreatedEvent;

public class ProductCreatedEvent extends CreatedEvent<ProductDTO> {

    public ProductCreatedEvent(ProductDTO dto) {
        super(dto);
    }

}
