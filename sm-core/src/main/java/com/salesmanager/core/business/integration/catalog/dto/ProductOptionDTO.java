package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductOptionDTO extends AbstractCatalogCrudDTO {

    private Long id;

    private String code;

    private List<ProductOptionDescriptionDTO> descriptions;

    @Getter
    @Setter
    public static class ProductOptionDescriptionDTO implements AbstractCatalogDTO {

        private Long id;

        private String name;

        private Integer languageId;

    }

}
