package com.salesmanager.shop.populator.order.transaction;

import com.salesmanager.core.business.utils.PriceUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;

import com.salesmanager.common.business.exception.ConversionException;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.order.transaction.ReadableTransaction;
import com.salesmanager.common.presentation.util.DateUtil;


public class ReadableTransactionPopulator extends AbstractDataPopulator<Transaction, ReadableTransaction> {

	
	private OrderService orderService;

	@Getter @Setter
	private PriceUtils priceUtils;
	
	@Override
	public ReadableTransaction populate(Transaction source, ReadableTransaction target, MerchantStore store,
			Language language) throws ConversionException {

		
		Validate.notNull(source,"PersistableTransaction must not be null");
		Validate.notNull(orderService,"OrderService must not be null");
		Validate.notNull(priceUtils,"priceUtils must not be null");
		
		if(target == null) {
			target = new ReadableTransaction();
		}
		
		
		try {
			

			target.setAmount(priceUtils.getStoreFormattedAmountWithCurrency(store, source.getAmount()));
			target.setDetails(source.getDetails());
			target.setPaymentType(source.getPaymentType());
			target.setTransactionType(source.getTransactionType());
			target.setTransactionDate(DateUtil.formatDate(source.getTransactionDate()));
			target.setId(source.getId());
			
			if(source.getOrder() != null) {
				target.setOrderId(source.getOrder().getId());

			}
			
			return target;
			
			
		
		} catch(Exception e) {
			throw new ConversionException(e);
		}
		
	}

	@Override
	protected ReadableTransaction createTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

}
