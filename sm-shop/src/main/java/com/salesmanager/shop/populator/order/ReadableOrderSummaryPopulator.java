package com.salesmanager.shop.populator.order;

import com.salesmanager.core.business.utils.PriceUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.salesmanager.common.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.order.ReadableOrderTotalSummary;
import com.salesmanager.shop.model.order.total.ReadableOrderTotal;
import com.salesmanager.common.presentation.util.LabelUtils;

public class ReadableOrderSummaryPopulator extends AbstractDataPopulator<OrderTotalSummary, ReadableOrderTotalSummary> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReadableOrderSummaryPopulator.class);

	@Getter @Setter
	private PriceUtils priceUtils;
	
	private LabelUtils messages;
	


	@Override
	public ReadableOrderTotalSummary populate(OrderTotalSummary source, ReadableOrderTotalSummary target,
			MerchantStore store, Language language) throws ConversionException {
		
		Validate.notNull(priceUtils,"priceUtils must be set");
		Validate.notNull(messages,"LabelUtils must be set");
		
		if(target==null) {
			target = new ReadableOrderTotalSummary();
		}
		
		try {
		
			if(source.getSubTotal() != null) {
				target.setSubTotal(priceUtils.getStoreFormattedAmountWithCurrency(store, source.getSubTotal()));
			}
			if(source.getTaxTotal()!=null) {
				target.setTaxTotal(priceUtils.getStoreFormattedAmountWithCurrency(store, source.getTaxTotal()));
			}
			if(source.getTotal() != null) {
				target.setTotal(priceUtils.getStoreFormattedAmountWithCurrency(store, source.getTotal()));
			}
			
			if(!CollectionUtils.isEmpty(source.getTotals())) {
				ReadableOrderTotalPopulator orderTotalPopulator = new ReadableOrderTotalPopulator();
				orderTotalPopulator.setMessages(messages);
				orderTotalPopulator.setPriceUtils(priceUtils);
				for(OrderTotal orderTotal : source.getTotals()) {
					ReadableOrderTotal t = new ReadableOrderTotal();
					orderTotalPopulator.populate(orderTotal, t, store, language);
					target.getTotals().add(t);
				}
			}
			
		
		} catch(Exception e) {
			LOGGER.error("Error during amount formatting " + e.getMessage());
			throw new ConversionException(e);
		}
		
		return target;
		
	}

	@Override
	protected ReadableOrderTotalSummary createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public LabelUtils getMessages() {
		return messages;
	}

	public void setMessages(LabelUtils messages) {
		this.messages = messages;
	}

}
