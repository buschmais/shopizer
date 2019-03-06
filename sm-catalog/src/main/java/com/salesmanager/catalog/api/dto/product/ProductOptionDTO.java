package com.salesmanager.catalog.api.dto.product;

import com.salesmanager.catalog.api.dto.AbstractCatalogCrudDTO;
import com.salesmanager.catalog.api.dto.AbstractCatalogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ProductOptionDTO extends AbstractCatalogCrudDTO {

    private Long id;

    private String code;

    private List<ProductOptionDescriptionDTO> descriptions;

    @AllArgsConstructor
    @Getter
    public static class ProductOptionDescriptionDTO implements AbstractCatalogDTO {

        private Long id;

        private String name;

        private Integer languageId;

    }

}
