package com.salesmanager.catalog.api.event.product;

import com.salesmanager.catalog.api.dto.product.ProductOptionValueDTO;
import com.salesmanager.common.model.integration.UpdatedEvent;

public class ProductOptionValueUpdatedEvent extends UpdatedEvent<ProductOptionValueDTO> {

    public ProductOptionValueUpdatedEvent(ProductOptionValueDTO dto) {
        super(dto);
    }

}
