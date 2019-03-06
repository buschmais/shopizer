package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DimensionDTO implements AbstractCatalogDTO {

    private Double width;
    private Double length;
    private Double height;
    private Double weight;

}
