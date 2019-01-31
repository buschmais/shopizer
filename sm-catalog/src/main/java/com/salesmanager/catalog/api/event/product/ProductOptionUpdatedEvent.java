package com.salesmanager.catalog.api.event.product;

import com.salesmanager.catalog.api.dto.product.ProductOptionDTO;
import com.salesmanager.common.model.integration.UpdatedEvent;

public class ProductOptionUpdatedEvent extends UpdatedEvent<ProductOptionDTO> {

    public ProductOptionUpdatedEvent(ProductOptionDTO dto) {
        super(dto);
    }

}
