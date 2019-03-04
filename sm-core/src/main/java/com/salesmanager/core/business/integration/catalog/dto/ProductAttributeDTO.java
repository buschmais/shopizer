package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAttributeDTO extends AbstractCatalogDTO {

    private Long id;
    private Double price;
    private Boolean free;
    private Double weight;
    private Long productOptionId;
    private Long productOptionValueId;

}