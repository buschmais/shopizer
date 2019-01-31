package com.salesmanager.catalog.api.dto.product;

import com.salesmanager.catalog.api.dto.AbstractCatalogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductDescriptionDTO extends AbstractCatalogDTO {

    private final Long id;
    private final String name;
    private final String seUrl;
    private final Long languageId;

}
