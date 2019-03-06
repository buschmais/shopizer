package com.salesmanager.catalog.api.dto.product;

import com.salesmanager.catalog.api.dto.AbstractCatalogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class ProductPriceDTO implements AbstractCatalogDTO {

    private String code;
    private String productPriceType;
    private Boolean defaultPrice;
    private String name;
    private Double productPriceSpecialAmount;
    private Date productPriceSpecialStartDate;
    private Date productPriceSpecialEndDate;

}
