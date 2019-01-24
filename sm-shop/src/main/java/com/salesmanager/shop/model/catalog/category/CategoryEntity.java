package com.salesmanager.shop.model.catalog.category;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class CategoryEntity extends Category implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private int sortOrder;
	@Getter @Setter
	private boolean visible;
	@Getter @Setter
	private boolean featured;
	@Getter @Setter
	private String lineage;
	@Getter @Setter
	private int depth;
	@Getter @Setter
	private Category parent;

}
