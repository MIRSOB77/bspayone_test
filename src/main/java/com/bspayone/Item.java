package com.bspayone;

import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import javax.money.Monetary;
import lombok.Data;
import org.javamoney.moneta.Money;

@Data
public class Item {
    private String sku;
    private String title;
    private UnitType unitType;
    private Money price;

    //only in use for measurements like liters or kg
    private double size;

    private Set<Discount> discounts = Sets.newHashSet();

    public Item(String sku, String title, UnitType unitType, double price, String currencyCode){
        this.sku = sku;
        this.title = title;
        this.unitType = unitType;
        this.price = Money.of(new BigDecimal(price), Monetary.getCurrency("EUR"));
    }

    public void addAction(Discount action){
        discounts.add(action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Item)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(getSku(), item.getSku()) &&
                getUnitType() == item.getUnitType();
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), getSku(), getUnitType());
    }
}
