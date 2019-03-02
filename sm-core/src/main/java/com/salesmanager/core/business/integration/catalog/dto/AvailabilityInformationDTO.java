package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AvailabilityInformationDTO extends AbstractCatalogDTO {

    private boolean available;
    private boolean shippable;
    private boolean virtual;

}
