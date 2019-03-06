package com.salesmanager.catalog.api.dto.product;

import com.salesmanager.catalog.api.dto.AbstractCatalogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AvailabilityInformationDTO implements AbstractCatalogDTO {

    private boolean available;
    private boolean shippable;
    private boolean virtual;

}
