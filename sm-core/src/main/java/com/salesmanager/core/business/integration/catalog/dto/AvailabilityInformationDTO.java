package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AvailabilityInformationDTO implements AbstractCatalogDTO {

    private boolean available;
    private boolean shippable;
    private boolean virtual;

}
