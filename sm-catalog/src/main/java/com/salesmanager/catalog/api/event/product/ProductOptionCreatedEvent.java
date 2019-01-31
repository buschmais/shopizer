package com.salesmanager.catalog.api.event.product;

import com.salesmanager.catalog.api.dto.product.ProductOptionDTO;
import com.salesmanager.common.model.integration.CreatedEvent;

public class ProductOptionCreatedEvent extends CreatedEvent<ProductOptionDTO> {

    public ProductOptionCreatedEvent(ProductOptionDTO dto) {
        super(dto);
    }

}
