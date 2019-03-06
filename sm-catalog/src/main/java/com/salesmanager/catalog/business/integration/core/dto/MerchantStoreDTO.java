package com.salesmanager.catalog.business.integration.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MerchantStoreDTO extends AbstractCoreCrudDTO {

    private Integer id;

    private String code;

    private String currency;

    private String defaultLanguage;

    private String countryIsoCode;

    private boolean currencyFormatNational;

    private boolean useCache;

    private String storeTemplate;

    private String domainName;

    private List<String> languages;

}
