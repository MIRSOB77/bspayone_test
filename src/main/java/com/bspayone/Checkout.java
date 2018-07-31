package com.bspayone;

import com.bspayone.exceptions.CartException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.javamoney.moneta.Money;

public class Checkout {
    public static final CurrencyUnit CURRENCY = Monetary.getCurrency("EUR");
    public static final double MWST = 19.0;

    public Set<Item> getArticlesInStore() {
        return Sets.newHashSet(articlesInStore);
    }

    private Set<Item> articlesInStore;
    private Cart cart;

    private Checkout(){
        cart = new Cart();
    }

    public static Checkout create(Set<Item> currentOffers){
        Checkout checkout = new Checkout();
        checkout.articlesInStore = currentOffers;
//            currentOffers.stream().filter(
//                    e -> {
//                            Set<Discount> validActions = e.getDiscounts().stream().filter(a -> a.getUnitTypes().contains(e.getUnitType())).collect(Collectors.toSet());
//                            e.setDiscounts(validActions);
//                            return true;
//                          }).collect(Collectors.toSet());

        return checkout;
    }


    public void scan(String sku) throws CartException {
        Item scannedItem = this.articlesInStore.stream().filter(item -> item.getSku().equals(sku)).findFirst().orElseThrow(() -> new CartException("sku "  + sku + " doesn't exist"));

        cart.add(scannedItem);
    }

    public Money total(){
       
        return cart.total();
        
    }
}
