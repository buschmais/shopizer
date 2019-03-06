package com.salesmanager.catalog.business.integration.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TaxClassDTO extends AbstractCoreCrudDTO {

    private Long id;

    private String code;

    private String merchantStoreCode;

}
