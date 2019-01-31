package com.salesmanager.shop.populator.catalog;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.ProductPriceApi;
import com.salesmanager.core.business.services.catalog.ProductInfoService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.FinalPriceInfo;
import com.salesmanager.core.model.catalog.ProductDescriptionInfo;
import com.salesmanager.core.model.catalog.ProductImageInfo;
import com.salesmanager.core.model.catalog.ProductInfo;
import com.salesmanager.shop.model.catalog.product.ReadableImage;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.common.business.constants.Constants;
import com.salesmanager.common.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;

import java.math.BigDecimal;
import java.util.Set;


public class ReadableProductPopulator extends
		AbstractDataPopulator<ProductInfo, ReadableProduct> {
	
	private ProductPriceApi productPriceApi;

	@Getter @Setter
	private ProductApi productApi;

	@Getter @Setter
	private LanguageService languageService;

	@Getter @Setter
	private ProductInfoService productInfoService;

	public ProductPriceApi getProductPriceApi() {
		return productPriceApi;
	}

	public void setProductPriceApi(ProductPriceApi productPriceApi) {
		this.productPriceApi = productPriceApi;
	}

	@Override
	public ReadableProduct populate(ProductInfo source,
			ReadableProduct target, MerchantStore store, Language language)
			throws ConversionException {
		Validate.notNull(productPriceApi, "Requires to set ProductPriceApi");
		Validate.notNull(language, "Language cannot be null");
		
		try {
			

			ProductDescriptionInfo description = source.getProductDescription();
			
			Set<ProductDescriptionInfo> descriptions = source.getDescriptions();
			for(ProductDescriptionInfo desc : descriptions) {
				
				if(desc.getLanguageId() !=null && desc.getLanguageId() == language.getId().longValue()) {
					description = desc;
					break;
				}
				
			}

	
			target.setId(source.getId());
			target.setAvailable(source.getAvailabilityInformation().getAvailable());
			target.setProductVirtual(source.getAvailabilityInformation().getVirtual());

			if(description!=null) {
				com.salesmanager.shop.model.catalog.product.ProductDescription tragetDescription = new com.salesmanager.shop.model.catalog.product.ProductDescription();
				tragetDescription.setFriendlyUrl(description.getSeUrl());
				tragetDescription.setId(description.getId());
				tragetDescription.setLanguage(languageService.getById(description.getLanguageId().intValue()).getCode());
				target.setDescription(tragetDescription);
			}

			ProductImageInfo defaultImage = productInfoService.getDefaultImage(source.getId());
			if (defaultImage != null) {
				ReadableImage prdImage = new ReadableImage();
				prdImage.setImageName(defaultImage.getImageName());
				prdImage.setDefaultImage(defaultImage.isDefaultImage());

				prdImage.setImageUrl(defaultImage.getImageUrl());
				prdImage.setId(defaultImage.getId());
				target.setImage(prdImage);
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

			FinalPriceInfo price = productInfoService.getProductFinalPrice(source.getId(), null);

			target.setPrice(BigDecimal.valueOf(price.getFinalPrice()));

			target.setQuantity(productApi.getAvailabilityForRegion(source.getId(), Constants.ALL_REGIONS));
			
			
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
