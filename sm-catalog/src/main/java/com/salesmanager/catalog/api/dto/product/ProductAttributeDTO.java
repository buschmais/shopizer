package com.salesmanager.catalog.api.dto.product;

import com.salesmanager.catalog.api.dto.AbstractCatalogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductAttributeDTO extends AbstractCatalogDTO {

    private final Long id;
    private final Double price;
    private final Boolean free;
    private final Double weight;

}