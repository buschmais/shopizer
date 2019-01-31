package com.salesmanager.catalog.api.event.product;

import com.salesmanager.catalog.api.dto.product.ProductDTO;
import com.salesmanager.common.model.integration.DeletedEvent;

public class ProductDeletedEvent extends DeletedEvent<ProductDTO> {

    public ProductDeletedEvent(ProductDTO dto) {
        super(dto);
    }

}
