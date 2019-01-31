package com.salesmanager.shop.populator.order;

import com.salesmanager.core.business.repositories.catalog.ProductInfoRepository;
import com.salesmanager.core.model.catalog.ProductAttributeInfo;
import com.salesmanager.core.model.catalog.ProductInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

import com.salesmanager.common.business.exception.ConversionException;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.shop.model.order.PersistableOrderProduct;

public class ShoppingCartItemPopulator extends
		AbstractDataPopulator<PersistableOrderProduct, ShoppingCartItem> {
	

	private ShoppingCartService shoppingCartService;

	@Getter @Setter
	private ProductInfoRepository productInfoRepository;

	@Override
	public ShoppingCartItem populate(PersistableOrderProduct source,
			ShoppingCartItem target, MerchantStore store, Language language)
			throws ConversionException {
		Validate.notNull(shoppingCartService, "Requires to set shoppingCartService");
		
		ProductInfo product = productInfoRepository.findOne(source.getProduct().getId());
		if(source.getAttributes()!=null) {

			for(ProductAttributeInfo attr : source.getAttributes()) {
				ProductAttributeInfo attribute = null;
				for (ProductAttributeInfo productAttribute : product.getAttributes()) {
					if (productAttribute.getId().equals(attr.getId())) {
						attribute = productAttribute;
					}
				}
				if(attribute == null) {
					throw new ConversionException("ProductAttribute with id " + attr.getId() + " is not assigned to Product id " + source.getProduct().getId());
				}
				product.getAttributes().add(attribute);
			}
		}
		
		try {
			return shoppingCartService.populateShoppingCartItem(product);
		} catch (ServiceException e) {
			throw new ConversionException(e);
		}
		
	}

	@Override
	protected ShoppingCartItem createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setShoppingCartService(ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	public ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

}
