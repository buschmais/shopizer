package com.salesmanager.catalog.business.integration.core.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class CustomerDTO extends AbstractCoreCrudDTO {

    private Long id;

    private String nick;

    private String merchantStoreCode;

    private String firstName;

    private String lastName;

    private String defaultLanguageCode;

}
