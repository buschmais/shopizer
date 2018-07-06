package com.salesmanager.catalog.api;

import com.salesmanager.catalog.model.product.attribute.ProductAttribute;

public interface ProductAttributeApi {

    ProductAttribute getById(Long id);

}
