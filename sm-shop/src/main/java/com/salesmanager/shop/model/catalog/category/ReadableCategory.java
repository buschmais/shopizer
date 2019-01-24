package com.salesmanager.shop.model.catalog.category;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class ReadableCategory extends CategoryEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private CategoryDescription description;//one category based on language

}
