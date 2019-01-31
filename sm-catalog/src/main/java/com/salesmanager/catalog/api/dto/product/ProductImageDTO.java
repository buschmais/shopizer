package com.salesmanager.catalog.api.dto.product;

import com.salesmanager.catalog.api.dto.AbstractCatalogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductImageDTO extends AbstractCatalogDTO {

    private final Long id;
    private final String imageName;
    private final boolean defaultImage;
    private final String imageUrl;

}
