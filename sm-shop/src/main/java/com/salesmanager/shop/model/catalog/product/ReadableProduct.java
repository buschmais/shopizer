package com.salesmanager.shop.model.catalog.product;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class ReadableProduct extends ProductEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter @Setter
	private ProductDescription description;
	@Getter @Setter
	private ReadableImage image;

}
