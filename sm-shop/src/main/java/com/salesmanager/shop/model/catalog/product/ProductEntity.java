package com.salesmanager.shop.model.catalog.product;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * A product entity is used by services API
 * to populate or retrieve a Product entity
 * @author Carl Samson
 *
 */
public class ProductEntity extends Product implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter @Setter
	private BigDecimal price;
	@Getter @Setter
	private int quantity = 0;
	@Getter @Setter
	private String sku;
	@Getter @Setter
	private boolean productVirtual = false;
	@Getter @Setter
	private boolean available;

}
