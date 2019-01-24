package com.salesmanager.shop.model.catalog.manufacturer;

import com.salesmanager.common.presentation.model.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class Manufacturer extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private String code;

}
