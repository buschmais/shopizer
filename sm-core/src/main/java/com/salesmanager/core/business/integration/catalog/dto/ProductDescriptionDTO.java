package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductDescriptionDTO extends AbstractCatalogDTO {

    private Long id;
    private String name;
    private String seUrl;
    private Long languageId;

}
