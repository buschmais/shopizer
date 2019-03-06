package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDTO implements AbstractCatalogDTO {

    private Long id;
    private String imageName;
    private boolean defaultImage;
    private String imageUrl;

}