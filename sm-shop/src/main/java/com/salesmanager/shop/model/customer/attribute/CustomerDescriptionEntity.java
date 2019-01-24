package com.salesmanager.shop.model.customer.attribute;

import com.salesmanager.common.presentation.model.ShopEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public abstract class CustomerDescriptionEntity extends ShopEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String description;
    @Getter @Setter
    private String friendlyUrl;
    @Getter @Setter
    private String keyWords;
    @Getter @Setter
    private String highlights;
    @Getter @Setter
    private String metaDescription;
    @Getter @Setter
    private String title;

}
