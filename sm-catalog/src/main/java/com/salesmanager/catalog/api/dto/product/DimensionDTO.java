package com.salesmanager.catalog.api.dto.product;

import com.salesmanager.catalog.api.dto.AbstractCatalogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DimensionDTO extends AbstractCatalogDTO {

    private final Double width;
    private final Double length;
    private final Double height;
    private final Double weight;

}
