package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DimensionDTO extends AbstractCatalogDTO {

    private Double width;
    private Double length;
    private Double height;
    private Double weight;

}
