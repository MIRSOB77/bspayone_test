package com.bspayone;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Optional;
import javax.money.Monetary;
import org.javamoney.moneta.Money;
import static com.bspayone.UnitType.PACKAGE;
import static com.bspayone.UnitType.PIECE;

public class BuyManyAndSomeFreeDiscount extends Discount {

    private final Double discountInPercent;

    public BuyManyAndSomeFreeDiscount(int toBuy, int forFree) {
        super(Sets.newHashSet(PIECE,PACKAGE), toBuy);
        this.discountInPercent = forFree * 100.0 / toBuy;
    }

    @Override
    public Optional<Money> apply(Cart.CartEntry cartEntry) {
        Optional<Money> discount = Optional.empty();

        if (conditionMatches(cartEntry, cartEntry.getCount())) {
            discount = Optional.of(Money.of(cartEntry.getPricePerPiece().getNumber().doubleValue(), Checkout.CURRENCY).multiply(discountInPercent / 100));
        }
        return discount;
    }


    public boolean conditionMatches(Cart.CartEntry cartEntry, int current){
        boolean isValid = false;
        if (getUnitTypes().contains(cartEntry.getUnitType())) {
            if (getConditionCount() == current) {
                isValid = true;
            }
        }

        return isValid;
    }
}
