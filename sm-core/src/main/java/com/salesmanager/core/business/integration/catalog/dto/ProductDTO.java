package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO extends AbstractCatalogCrudDTO {

    private Long id;
    private String sku;
    private String name;
    private String manufacturerCode;

}
