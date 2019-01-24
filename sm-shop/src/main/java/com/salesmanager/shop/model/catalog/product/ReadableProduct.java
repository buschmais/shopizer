package com.salesmanager.shop.model.catalog.product;

import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.manufacturer.ReadableManufacturer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


public class ReadableProduct extends ProductEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter @Setter
	private ProductDescription description;
	@Getter @Setter
	private String finalPrice = "0";
	@Getter @Setter
	private String originalPrice = null;
	@Getter @Setter
	private boolean discounted = false;
	@Getter @Setter
	private ReadableImage image;
	@Getter @Setter
	private List<ReadableImage> images;
	@Getter @Setter
	private ReadableManufacturer manufacturer;
	@Getter @Setter
	private List<ReadableCategory> categories;
	@Getter @Setter
	private boolean canBePurchased = false;

}
