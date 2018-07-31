package com.bspayone;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.Value;
import org.javamoney.moneta.Money;

@Value
public class Cart {
    List<CartEntry> entries = Lists.newArrayList();
   
    public void add(Item item){
        Optional<CartEntry> cartEntry = entries.stream().
                                      filter(cE -> (cE.getSku().equals(item.getSku()) && !(cE.getClosed())))
                                      .findAny();
        if(cartEntry.isPresent()){
            cartEntry.get().plusOne();
        } else {
            createNewEntry(item);
        }
    }



    private CartEntry createNewEntry(Item item){
        CartEntry cartEntryNew = new CartEntry(item, this);
        cartEntryNew.plusOne();
        entries.add(cartEntryNew);

        return cartEntryNew;
    }

    public Money total(){
        Money total = Money.of(BigDecimal.ZERO, Checkout.CURRENCY);

        for(CartEntry cartEntry : entries){
            total = total.add(Money.of(cartEntry.getPricePerPiece().getNumber().doubleValue() * cartEntry.getCount(),Checkout.CURRENCY));
            total = total.subtract(cartEntry.getDiscount().multiply(cartEntry.getCount()));
        };

        //total = total.add( Money.of(new BigDecimal(total.getNumber().doubleValueExact() * (Checkout.MWST / 100.0)), Checkout.CURRENCY));

        return total;
    }
    


    @Getter
    public static class CartEntry {
        // count of items in cart under this entry
        int count;
        Money discount = Money.of(BigDecimal.ZERO, Checkout.CURRENCY);
        Boolean closed = false;

        //article attributes
        String sku;
        Set<Discount> discounts;
        Money pricePerPiece;
        double size;
        UnitType unitType;

        private Cart selfCart;



        public CartEntry(final Item item, Cart cart) {
            pricePerPiece = item.getPrice();
            sku = item.getSku();
            discounts = item.getDiscounts();
            size = item.getSize();
            unitType = item.getUnitType();

            selfCart = cart;
        }

        public void plusOne(){
            count++;

            Optional<CartEntry> closedEntry = selfCart.getEntries().stream().
                    filter(cE -> (cE.getSku().equals(sku) && cE.getClosed())).findFirst();

            Optional<Discount> action = discounts.stream().filter(a -> a.conditionMatches(this, count)).findFirst();

            Optional<Money> discountAmount;
            if(action.isPresent()) {
                discountAmount = action.get().apply(this);
                discountAmount.ifPresent(e -> {
                    discount = e;
                    this.closed = Boolean.TRUE;
                });
            } else {
                if(closedEntry.isPresent()){
                   
                    action = discounts.stream().filter(a -> a.conditionMatches(this, count+closedEntry.get().getCount())).findFirst();

                    if(action.isPresent()){
                        count += closedEntry.get().getCount();
                        selfCart.getEntries().remove(closedEntry.get());
                        discountAmount = action.get().apply(this);
                        discountAmount.ifPresent(e -> {
                            discount = e;
                            this.closed = Boolean.TRUE;
                        });

                    }
                }

            }

        }


        public String toString(){
            return sku + "    " + count + " * " + pricePerPiece.toString() + " = " + pricePerPiece.multiply(count).toString() + "\n"
                    +   (discount.isGreaterThan(Money.of(BigDecimal.ZERO, Checkout.CURRENCY)) ? "Discount" + "    " + discount.toString() : "");
        }

    }


}
