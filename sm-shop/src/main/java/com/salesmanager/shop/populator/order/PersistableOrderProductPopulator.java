package com.salesmanager.shop.populator.order;

import com.salesmanager.catalog.api.DigitalProductApi;
import com.salesmanager.common.business.exception.ConversionException;
import com.salesmanager.core.business.repositories.catalog.ProductInfoRepository;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.ProductAttributeInfo;
import com.salesmanager.core.model.catalog.ProductInfo;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderproduct.OrderProductAttribute;
import com.salesmanager.core.model.order.orderproduct.OrderProductDownload;
import com.salesmanager.core.model.order.orderproduct.OrderProductPrice;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.ApplicationConstants;
import com.salesmanager.shop.model.order.PersistableOrderProduct;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersistableOrderProductPopulator extends
		AbstractDataPopulator<PersistableOrderProduct, OrderProduct> {
	
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
	public OrderProduct populate(PersistableOrderProduct source, OrderProduct target,
			MerchantStore store, Language language) throws ConversionException {
		
		Validate.notNull(digitalProductApi,"digitalProductApi must be set");

		try {
			ProductInfo modelProduct = productInfoRepository.findOne(source.getProduct().getId());
			if(modelProduct==null) {
				throw new ConversionException("Cannot get product with id (productId) " + source.getProduct().getId());
			}
			
			if(modelProduct.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				throw new ConversionException("Invalid product id " + source.getProduct().getId());
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

			target.setOneTimeCharge(source.getPrice());
			target.setProductQuantity(source.getOrderedQuantity());
			target.setSku(source.getProduct().getSku());
			
			OrderProductPrice orderProductPrice = new OrderProductPrice();
			orderProductPrice.setDefaultPrice(true);
			orderProductPrice.setProductPrice(source.getPrice());
			orderProductPrice.setOrderProduct(target);
			

			
			Set<OrderProductPrice> prices = new HashSet<OrderProductPrice>();
			prices.add(orderProductPrice);

			/** DO NOT SUPPORT MUTIPLE PRICES **/
/*			//Other prices
			List<FinalPrice> otherPrices = finalPrice.getAdditionalPrices();
			if(otherPrices!=null) {
				for(FinalPrice otherPrice : otherPrices) {
					OrderProductPrice other = orderProductPrice(otherPrice);
					other.setOrderProduct(target);
					prices.add(other);
				}
			}*/
			
			target.setPrices(prices);
			
			//OrderProductAttribute
			List<ProductAttributeInfo> attributeItems = source.getAttributes();
			if(!CollectionUtils.isEmpty(attributeItems)) {
				Set<OrderProductAttribute> attributes = new HashSet<OrderProductAttribute>();
				for(ProductAttributeInfo attribute : attributeItems) {
					OrderProductAttribute orderProductAttribute = new OrderProductAttribute();
					orderProductAttribute.setOrderProduct(target);

					orderProductAttribute.setProductAttributeIsFree(attribute.getFree());
					orderProductAttribute.setProductAttributeName(attribute.getProductOption().getDescriptions().iterator().next().getName());
					orderProductAttribute.setProductAttributeValueName(attribute.getProductOptionValue().getDescriptions().iterator().next().getName());
					orderProductAttribute.setProductAttributePrice(BigDecimal.valueOf(attribute.getPrice()));
					orderProductAttribute.setProductAttributeWeight(BigDecimal.valueOf(attribute.getWeight()));
					orderProductAttribute.setProductOptionId(attribute.getProductOption().getId());
					orderProductAttribute.setProductOptionValueId(attribute.getProductOptionValue().getId());
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

}
