package com.salesmanager.shop.model.catalog;

import com.salesmanager.common.presentation.model.ShopEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public abstract class CatalogEntity extends ShopEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter @Setter
	private String friendlyUrl;

}
