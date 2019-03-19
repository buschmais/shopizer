package com.salesmanager.core.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MerchantStoreDTO extends AbstractCoreCrudDTO {

    private Integer id;

    private String name;

    private String code;

    private String currency;

    private String defaultLanguage;

    private String countryIsoCode;

    private boolean currencyFormatNational;

    private boolean useCache;

    private String storeTemplate;

    private String domainName;

    private String weightUnitCode;

    private String sizeUnitCode;

    private List<String> languages;

}
