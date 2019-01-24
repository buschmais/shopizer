package com.salesmanager.shop.model.catalog.product;

import com.salesmanager.common.presentation.model.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class ReadableImage extends Entity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter @Setter
	private String imageName;
	@Getter @Setter
	private String imageUrl;
	@Getter @Setter
	private String externalUrl;
	@Getter @Setter
	private String videoUrl;
	@Getter @Setter
	private int imageType;
	@Getter @Setter
	private boolean defaultImage;

}
