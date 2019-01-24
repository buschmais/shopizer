package com.salesmanager.shop.model.catalog.product;

import com.salesmanager.catalog.model.product.ProductCondition;
import com.salesmanager.catalog.model.product.RentalStatus;
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
	private boolean preOrder = false;
	@Getter @Setter
	private boolean productVirtual = false;
	@Getter @Setter
	private int quantityOrderMaximum =-1;//default unlimited
	@Getter @Setter
	private int quantityOrderMinimum = 1;//default 1
	@Getter @Setter
	private boolean available;
	@Getter @Setter
	private BigDecimal productLength;
	@Getter @Setter
	private BigDecimal productWidth;
	@Getter @Setter
	private BigDecimal productHeight;
	@Getter @Setter
	private BigDecimal productWeight;
	@Getter @Setter
	private Double rating = 0D;
	@Getter @Setter
	private int ratingCount;
	@Getter @Setter
	private int sortOrder;
	@Getter @Setter
	private String dateAvailable;
	@Getter @Setter
	private String refSku;
	@Getter @Setter
	private ProductCondition condition;
	
	/**
	 * RENTAL additional fields
	 * @return
	 */
	@Getter @Setter
	private int rentalDuration;
	@Getter @Setter
	private int rentalPeriod;
	@Getter @Setter
	private RentalStatus rentalStatus;

	/**
	 * End RENTAL fields
	 * @return
	 */

}
