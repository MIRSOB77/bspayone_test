package com.bspayone;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import lombok.Data;
import org.javamoney.moneta.Money;

@Data
public abstract class Discount {
    private Set<UnitType> unitTypes = Sets.newHashSet();
    private Integer conditionCount;

    public Discount(UnitType unitType){
        this.unitTypes.add(unitType);
    }

    public Discount(UnitType unitType, Integer minimumCount){
        this.unitTypes.add(unitType);
        this.conditionCount = minimumCount;
    }

    public Discount(Set<UnitType> unitType, Integer minimumCount){
        this.unitTypes.addAll(unitType);
        conditionCount = minimumCount;
    }

    public abstract Optional<Money> apply(Cart.CartEntry cartEntry);
    public abstract boolean conditionMatches(Cart.CartEntry cartEntry, int current);

}
