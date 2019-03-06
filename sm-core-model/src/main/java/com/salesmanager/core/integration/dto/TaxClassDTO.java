package com.salesmanager.core.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaxClassDTO extends AbstractCoreCrudDTO {

    private Long id;

    private String code;

    private String merchantStoreCode;

}
