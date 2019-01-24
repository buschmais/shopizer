package com.salesmanager.shop.populator.catalog;

import com.salesmanager.catalog.api.CatalogImageFilePathApi;
import com.salesmanager.catalog.api.ProductPriceApi;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.availability.ProductAvailability;
import com.salesmanager.catalog.model.product.description.ProductDescription;
import com.salesmanager.catalog.model.product.image.ProductImage;
import com.salesmanager.catalog.model.product.price.FinalPrice;
import com.salesmanager.shop.model.catalog.product.ReadableImage;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.common.business.constants.Constants;
import com.salesmanager.common.business.exception.ConversionException;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import org.apache.commons.lang.Validate;
import java.util.Set;


public class ReadableProductPopulator extends
		AbstractDataPopulator<Product, ReadableProduct> {
	
	private ProductPriceApi productPriceApi;

	private CatalogImageFilePathApi imageFilePathApi;

	private CustomerService customerService;

	public CatalogImageFilePathApi getImageFilePathApi() {
		return imageFilePathApi;
	}

	public void setImageFilePathApi(CatalogImageFilePathApi imageFilePathApi) {
		this.imageFilePathApi = imageFilePathApi;
	}

	public ProductPriceApi getProductPriceApi() {
		return productPriceApi;
	}

	public void setProductPriceApi(ProductPriceApi productPriceApi) {
		this.productPriceApi = productPriceApi;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Override
	public ReadableProduct populate(Product source,
			ReadableProduct target, MerchantStore store, Language language)
			throws ConversionException {
		Validate.notNull(productPriceApi, "Requires to set ProductPriceApi");
		Validate.notNull(language, "Language cannot be null");
		
		try {
			

			ProductDescription description = source.getProductDescription();
			
			Set<ProductDescription> descriptions = source.getDescriptions();
			for(ProductDescription desc : descriptions) {
				
				if(desc.getLanguage()!=null && desc.getLanguage().getId().intValue() == language.getId().intValue()) {
					description = desc;
					break;
				}
				
			}

	
			target.setId(source.getId());
			target.setAvailable(source.isAvailable());
			target.setProductVirtual(source.getProductVirtual());

			if(description!=null) {
				com.salesmanager.shop.model.catalog.product.ProductDescription tragetDescription = new com.salesmanager.shop.model.catalog.product.ProductDescription();
				tragetDescription.setFriendlyUrl(description.getSeUrl());
				tragetDescription.setId(description.getId());
				tragetDescription.setLanguage(description.getLanguage().getCode());
				target.setDescription(tragetDescription);
			}
			
			Set<ProductImage> images = source.getImages();
			if(images!=null && images.size()>0) {
				
				String contextPath = imageFilePathApi.getContextPath();
				
				for(ProductImage img : images) {
					ReadableImage prdImage = new ReadableImage();
					prdImage.setImageName(img.getProductImage());
					prdImage.setDefaultImage(img.isDefaultImage());

					StringBuilder imgPath = new StringBuilder();
					imgPath.append(contextPath != null ? contextPath : "").append(imageFilePathApi.buildProductImageUtils(store.toDTO(), source.getSku(), img.getProductImage()));

					prdImage.setImageUrl(imgPath.toString());
					prdImage.setId(img.getId());
					prdImage.setImageType(img.getImageType());
					if(img.getProductImageUrl()!=null){
						prdImage.setExternalUrl(img.getProductImageUrl());
					}
					if(img.getImageType()==1 && img.getProductImageUrl()!=null) {//video
						prdImage.setVideoUrl(img.getProductImageUrl());
					}
					
					if(prdImage.isDefaultImage()) {
						target.setImage(prdImage);
					}
				}
			}

			
			//remove products from invisible category -> set visible = false
/*			Set<Category> categories = source.getCategories();
			boolean isVisible = true;
			if(!CollectionUtils.isEmpty(categories)) {
				for(Category c : categories) {
					if(c.isVisible()) {
						isVisible = true;
						break;
					} else {
						isVisible = false;
					}
				}
			}*/
			
			//target.setVisible(isVisible);
			
	
			target.setSku(source.getSku());
	
			FinalPrice price = productPriceApi.calculateProductPrice(source);

			target.setPrice(price.getFinalPrice());


			//availability
			for(ProductAvailability availability : source.getAvailabilities()) {
				if(availability.getRegion().equals(Constants.ALL_REGIONS)) {//TODO REL 2.1 accept a region
					target.setQuantity(availability.getProductQuantity());
				}
			}
			
			
			return target;
		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	}




	@Override
	protected ReadableProduct createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
