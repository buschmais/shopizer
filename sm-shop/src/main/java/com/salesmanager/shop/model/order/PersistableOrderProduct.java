package com.salesmanager.shop.model.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.salesmanager.core.model.catalog.ProductAttributeInfo;

public class PersistableOrderProduct extends OrderProductEntity implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal price;//specify final price
	private List<ProductAttributeInfo> attributes;//may have attributes



	public void setAttributes(List<ProductAttributeInfo> attributes) {
		this.attributes = attributes;
	}

	public List<ProductAttributeInfo> getAttributes() {
		return attributes;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
