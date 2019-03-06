package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDescriptionDTO implements AbstractCatalogDTO {

    private Long id;
    private String name;
    private String seUrl;
    private Long languageId;

}
