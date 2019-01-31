package com.salesmanager.catalog.api.event.product;

import com.salesmanager.catalog.api.dto.product.ProductOptionDTO;
import com.salesmanager.common.model.integration.DeletedEvent;

public class ProductOptionDeletedEvent extends DeletedEvent<ProductOptionDTO> {

    public ProductOptionDeletedEvent(ProductOptionDTO dto) {
        super(dto);
    }

}
