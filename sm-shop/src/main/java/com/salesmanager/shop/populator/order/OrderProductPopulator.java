package com.salesmanager.shop.populator.order;

import com.salesmanager.catalog.api.DigitalProductApi;
import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.ProductAttributeApi;
import com.salesmanager.common.business.exception.ConversionException;
import com.salesmanager.core.business.repositories.catalog.ProductInfoRepository;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.catalog.model.product.price.FinalPrice;
import com.salesmanager.catalog.model.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.ProductAttributeInfo;
import com.salesmanager.core.model.catalog.ProductInfo;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderproduct.OrderProductAttribute;
import com.salesmanager.core.model.order.orderproduct.OrderProductDownload;
import com.salesmanager.core.model.order.orderproduct.OrderProductPrice;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.shop.constants.ApplicationConstants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderProductPopulator extends
		AbstractDataPopulator<ShoppingCartItem, OrderProduct> {
	
	private DigitalProductApi digitalProductApi;

	@Getter @Setter
	private ProductInfoRepository productInfoRepository;

	public DigitalProductApi getDigitalProductApi() {
		return digitalProductApi;
	}

	public void setDigitalProductApi(DigitalProductApi digitalProductApi) {
		this.digitalProductApi = digitalProductApi;
	}

	/**
	 * Converts a ShoppingCartItem carried in the ShoppingCart to an OrderProduct
	 * that will be saved in the system
	 */
	@Override
	public OrderProduct populate(ShoppingCartItem source, OrderProduct target,
			MerchantStore store, Language language) throws ConversionException {
		
		Validate.notNull(digitalProductApi,"digitalProductApi must be set");

		try {
			ProductInfo modelProduct = productInfoRepository.findOne(source.getProductId());
			if(modelProduct==null) {
				throw new ConversionException("Cannot get product with id (productId) " + source.getProductId());
			}
			
			if(modelProduct.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				throw new ConversionException("Invalid product id " + source.getProductId());
			}

			String digitalProductFileName = digitalProductApi.getFileNameByProduct(store.toDTO(), modelProduct.getId());
			
			if(digitalProductFileName != null) {
				OrderProductDownload orderProductDownload = new OrderProductDownload();	
				orderProductDownload.setOrderProductFilename(digitalProductFileName);
				orderProductDownload.setOrderProduct(target);
				orderProductDownload.setDownloadCount(0);
				orderProductDownload.setMaxdays(ApplicationConstants.MAX_DOWNLOAD_DAYS);
				target.getDownloads().add(orderProductDownload);
			}

			target.setOneTimeCharge(source.getItemPrice());	
			target.setProductName(source.getProduct().getDescriptions().iterator().next().getName());
			target.setProductQuantity(source.getQuantity());
			target.setSku(source.getProduct().getSku());
			
			FinalPrice finalPrice = source.getFinalPrice();
			if(finalPrice==null) {
				throw new ConversionException("Object final price not populated in shoppingCartItem (source)");
			}
			//Default price
			OrderProductPrice orderProductPrice = orderProductPrice(finalPrice);
			orderProductPrice.setOrderProduct(target);
			
			Set<OrderProductPrice> prices = new HashSet<OrderProductPrice>();
			prices.add(orderProductPrice);

			//Other prices
			List<FinalPrice> otherPrices = finalPrice.getAdditionalPrices();
			if(otherPrices!=null) {
				for(FinalPrice otherPrice : otherPrices) {
					OrderProductPrice other = orderProductPrice(otherPrice);
					other.setOrderProduct(target);
					prices.add(other);
				}
			}
			
			target.setPrices(prices);
			
			//OrderProductAttribute
			Set<ShoppingCartAttributeItem> attributeItems = source.getAttributes();
			if(!CollectionUtils.isEmpty(attributeItems)) {
				Set<OrderProductAttribute> attributes = new HashSet<OrderProductAttribute>();
				for(ShoppingCartAttributeItem attribute : attributeItems) {
					OrderProductAttribute orderProductAttribute = new OrderProductAttribute();
					orderProductAttribute.setOrderProduct(target);
					Long id = attribute.getProductAttributeId();
					ProductAttributeInfo attr = null;
					for (ProductAttributeInfo productAttribute : modelProduct.getAttributes()) {
						if (productAttribute.getId().equals(id)) {
							attr = productAttribute;
						}
					}
					if(attr==null) {
						throw new ConversionException("Attribute id " + id + " does not exists");
					}
					
					orderProductAttribute.setProductAttributeIsFree(attr.getFree());
					orderProductAttribute.setProductAttributeName(attr.getProductOption().getDescriptions().iterator().next().getName());
					orderProductAttribute.setProductAttributeValueName(attr.getProductOptionValue().getDescriptions().iterator().next().getName());
					orderProductAttribute.setProductAttributePrice(BigDecimal.valueOf(attr.getPrice()));
					orderProductAttribute.setProductAttributeWeight(BigDecimal.valueOf(attr.getWeight()));
					orderProductAttribute.setProductOptionId(attr.getProductOption().getId());
					orderProductAttribute.setProductOptionValueId(attr.getProductOptionValue().getId());
					attributes.add(orderProductAttribute);
				}
				target.setOrderAttributes(attributes);
			}

			
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		return target;
	}

	@Override
	protected OrderProduct createTarget() {
		return null;
	}

	private OrderProductPrice orderProductPrice(FinalPrice price) {
		
		OrderProductPrice orderProductPrice = new OrderProductPrice();
		
		ProductPrice productPrice = price.getProductPrice();
		
		orderProductPrice.setDefaultPrice(productPrice.isDefaultPrice());

		orderProductPrice.setProductPrice(price.getFinalPrice());
		orderProductPrice.setProductPriceCode(productPrice.getCode());
		if(productPrice.getDescriptions()!=null && productPrice.getDescriptions().size()>0) {
			orderProductPrice.setProductPriceName(productPrice.getDescriptions().iterator().next().getName());
		}
		if(price.isDiscounted()) {
			orderProductPrice.setProductPriceSpecial(productPrice.getProductPriceSpecialAmount());
			orderProductPrice.setProductPriceSpecialStartDate(productPrice.getProductPriceSpecialStartDate());
			orderProductPrice.setProductPriceSpecialEndDate(productPrice.getProductPriceSpecialEndDate());
		}
		
		return orderProductPrice;
	}


}
