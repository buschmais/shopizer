package com.salesmanager.shop.model.catalog.product.attribute;

import com.salesmanager.common.presentation.model.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class ProductOptionValue extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private String code;

}
