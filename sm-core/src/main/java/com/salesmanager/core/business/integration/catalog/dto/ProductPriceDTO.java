package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProductPriceDTO implements AbstractCatalogDTO {

    private String code;
    private String productPriceType;
    private Boolean defaultPrice;
    private String name;
    private Double productPriceSpecialAmount;
    private Date productPriceSpecialStartDate;
    private Date productPriceSpecialEndDate;

}
