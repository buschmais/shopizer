package com.salesmanager.core.integration.tax;

import com.salesmanager.core.integration.AbstractCoreCrudDTO;
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
