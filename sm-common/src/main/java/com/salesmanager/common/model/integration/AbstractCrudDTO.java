package com.salesmanager.common.model.integration;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public abstract class AbstractCrudDTO implements AbstractDTO {

    private EventType eventType;

    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
}
