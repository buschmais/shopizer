package com.salesmanager.catalog.api.event.product;

import com.salesmanager.catalog.api.dto.product.ProductDTO;
import com.salesmanager.common.model.integration.UpdatedEvent;

public class ProductUpdatedEvent extends UpdatedEvent<ProductDTO> {

    public ProductUpdatedEvent(ProductDTO dto) {
        super(dto);
    }

}
