package com.salesmanager.shop.model.catalog.manufacturer;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class ManufacturerEntity extends Manufacturer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private int order;


}
