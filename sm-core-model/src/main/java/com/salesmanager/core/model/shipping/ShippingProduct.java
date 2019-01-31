package com.salesmanager.core.model.shipping;

import com.salesmanager.catalog.model.product.price.FinalPrice;
import com.salesmanager.core.model.catalog.ProductInfo;

public class ShippingProduct {
	
	public ShippingProduct(ProductInfo product) {
		this.product = product;

	}
	
	private int quantity = 1;
	private ProductInfo product;
	
	private FinalPrice finalPrice;
	
	
	
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
	public FinalPrice getFinalPrice() {
		return finalPrice;
	}
	public void setFinalPrice(FinalPrice finalPrice) {
		this.finalPrice = finalPrice;
	}

}
