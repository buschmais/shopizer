package com.salesmanager.catalog.api.event.product;

import com.salesmanager.catalog.api.dto.product.ProductOptionValueDTO;
import com.salesmanager.common.model.integration.DeletedEvent;

public class ProductOptionValueDeletedEvent extends DeletedEvent<ProductOptionValueDTO> {

    public ProductOptionValueDeletedEvent(ProductOptionValueDTO dto) {
        super(dto);
    }

}
