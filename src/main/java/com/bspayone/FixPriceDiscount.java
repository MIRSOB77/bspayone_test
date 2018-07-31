package com.bspayone;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import javax.money.Monetary;
import org.javamoney.moneta.Money;

public class FixPriceDiscount extends Discount {
    private final Money fixPrice;
    
    public FixPriceDiscount(Money fixPrice, Integer minimumCount) {
        super(UnitType.PIECE, minimumCount);
        this.fixPrice = fixPrice;
    }

    public Optional<Money> apply(Cart.CartEntry cartEntry) {
        Optional<Money> discount = Optional.empty();
        if (conditionMatches(cartEntry, cartEntry.getCount())) {
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
        if(getUnitTypes().contains(cartEntry.getUnitType())){
            if (getConditionCount() == current) {
                isValid = true;
            }
        }

        return  isValid;
    }


}
