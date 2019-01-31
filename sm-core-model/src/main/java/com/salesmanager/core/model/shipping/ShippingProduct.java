package com.salesmanager.core.model.shipping;

import com.salesmanager.core.model.catalog.FinalPriceInfo;
import com.salesmanager.core.model.catalog.ProductInfo;

public class ShippingProduct {
	
	public ShippingProduct(ProductInfo product) {
		this.product = product;

	}
	
	private int quantity = 1;
	private ProductInfo product;
	
	private FinalPriceInfo finalPrice;
	
	
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setProduct(ProductInfo product) {
		this.product = product;
	}
	public ProductInfo getProduct() {
		return product;
	}
	public FinalPriceInfo getFinalPrice() {
		return finalPrice;
	}
	public void setFinalPrice(FinalPriceInfo finalPrice) {
		this.finalPrice = finalPrice;
	}

}
