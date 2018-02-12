package com.redhat.coolstore.cart.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redhat.coolstore.cart.model.Product;
import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.model.ShoppingCartItem;

@Component
public class ShoppingCartServiceImpl implements ShoppingCartService {

	private Map<String,ShoppingCart> db = new HashMap<String,ShoppingCart>();
	
	@Autowired
    private CatalogService catalogService;

    @Autowired
    private PriceCalculationService priceCalculationService;
    
	@Override
	public ShoppingCart calculateCartPrice(ShoppingCart sc) {
		priceCalculationService.priceShoppingCart(sc);
		return sc;
	}

	@Override
	public ShoppingCart getShoppingCart(String cartId) {
		System.out.println("### getShoppingCart: '" + cartId + "'");
		ShoppingCart cart = db.get(cartId);
		if (cart == null) {
			cart = new ShoppingCart();
			cart.setId(cartId);
			db.put(cartId, cart);
			System.out.println("### created: '" + cart.getId() + "'");
		}
		else {
			System.out.println("### exists: '" + cart.getId() + "'");
		}
		return cart;
	}

	@Override
	public ShoppingCart addToCart(String cartId, String itemId, int quantity) {

		ShoppingCart cart = getShoppingCart(cartId);
		if (quantity <= 0) {
            return cart;
        }
		Product p = catalogService.getProduct(itemId);
		if (p == null) {
            return cart;			
		}

		ShoppingCartItem item = null;
		for (ShoppingCartItem  i : cart.getShoppingCartItemList()) {
			if (i.getProduct().getItemId().compareTo(itemId) == 0) {
				item = i;
				i.setQuantity(i.getQuantity() + quantity);
			}
		}

		if (item == null) {		
			item = new ShoppingCartItem();
			item.setProduct(p);
			item.setPrice(p.getPrice());
			item.setQuantity(quantity);
			cart.addShoppingCartItem(item);
		}		
		
		priceCalculationService.priceShoppingCart(cart);
		
		return cart;
	}

	@Override
	public ShoppingCart removeFromCart(String cartId, String itemId, int quantity) {

		ShoppingCart cart = getShoppingCart(cartId);
		if (quantity <= 0) {
            return cart;
        }

		ShoppingCartItem item = null;
		for (ShoppingCartItem  i : cart.getShoppingCartItemList()) {
			if (i.getProduct().getItemId().compareTo(itemId) == 0) {
				item = i;
			}
		}
		
		if (item != null) {
			if (item.getQuantity() <= quantity) {
				cart.removeShoppingCartItem(item);
			}
			else {
				item.setQuantity(item.getQuantity() - quantity);
			}
			priceCalculationService.priceShoppingCart(cart);
		}
		
		return cart;
	}

}
