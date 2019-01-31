package com.salesmanager.core.model.catalog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductPriceInfo {

    private String code;
    private String productPriceType;
    private Boolean defaultPrice;
    private String name;
    private Double productPriceSpecialAmount;
    private Date productPriceSpecialStartDate;
    private Date productPriceSpecialEndDate;

}
