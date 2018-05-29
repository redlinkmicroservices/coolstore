package com.redhat.coolstore.cart.service;

import org.springframework.stereotype.Component;

import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.model.ShoppingCartItem;

@Component
public class PriceCalculationServiceImpl implements PriceCalculationService {

	@Override
	public void priceShoppingCart(ShoppingCart sc) {
		double itemTotal = 0;
		for (ShoppingCartItem item : sc.getShoppingCartItemList()) {
			itemTotal += item.getPrice() * item.getQuantity();
		}
		sc.setCartItemTotal(itemTotal);
		
		if (sc.getCartItemTotal() < 0.1)
			sc.setShippingTotal(0);
		else if (sc.getCartItemTotal() < 25)
			sc.setShippingTotal(2.99);
		else if (sc.getCartItemTotal() < 50)
			sc.setShippingTotal(4.99);
		else if (sc.getCartItemTotal() < 75)
			sc.setShippingTotal(6.99);
		else
			sc.setShippingTotal(0);
		
		sc.setCartTotal(sc.getCartItemTotal() + sc.getShippingTotal());
	}

}
