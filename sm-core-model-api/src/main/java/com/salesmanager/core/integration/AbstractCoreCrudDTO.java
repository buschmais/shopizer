package com.salesmanager.core.integration;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public abstract class AbstractCoreCrudDTO extends AbstractCoreDTO {

    private EventType eventType;

    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }

}
