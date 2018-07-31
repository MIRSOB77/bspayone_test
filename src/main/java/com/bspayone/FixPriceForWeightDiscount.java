package com.bspayone;

import java.math.BigDecimal;
import java.util.Optional;
import org.javamoney.moneta.Money;

public class FixPriceForWeightDiscount extends Discount {
    private final Money fixPrice;
    private final Double minimumWeight;

    public FixPriceForWeightDiscount(Money fixPrice, Double minimumWeight) {
        super(UnitType.KG);
        this.fixPrice = fixPrice;
        this.minimumWeight = minimumWeight;
    }

    public Optional<Money> apply(Cart.CartEntry cartEntry) {
        Optional<Money> discount = Optional.empty();
        if(conditionMatches(cartEntry, cartEntry.getCount())) {
            Money moneyAmount =
                    Money.of(
                            cartEntry.pricePerPiece.getNumber().doubleValue() - fixPrice.divide(cartEntry.getCount()).getNumber().doubleValue(),
                            Checkout.CURRENCY);

            if (moneyAmount.isGreaterThan(Money.of(BigDecimal.ZERO, Checkout.CURRENCY))) {
                discount = Optional.of(moneyAmount);
            }
        }
    

        return discount;
    }


    public boolean conditionMatches(Cart.CartEntry cartEntry, int current){
        boolean isValid = false;
        if(getUnitTypes().contains(cartEntry.getUnitType())) {
            if (minimumWeight >= (cartEntry.getCount() * cartEntry.getSize())) {
                isValid = true;
            }
        }

        return  isValid;
    }
}
