package com.bspayone;

import com.bspayone.exceptions.CartException;
import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import org.javamoney.moneta.Money;
import org.junit.Assert;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckoutTest {

    @org.junit.Test
    public void test_create_checkout() {
        Item apples = new Item("A", "Apple", UnitType.PACKAGE, 1.99, "EUR");
        Item mango = new Item("B", "Mango", UnitType.PIECE, 1.00, "EUR");
        Item juice = new Item("C", "Apple in kg", UnitType.KG, 1.19, "EUR");

        apples.addAction(new BuyManyAndSomeFreeDiscount(3, 1));

        Set<Item> itemSet = Sets.newHashSet(apples, mango, juice);

        Checkout.create(itemSet);
    }

    @org.junit.Test(expected = CartException.class)
    public void test_scan_notfound() {
        Item apples = new Item("A", "Apple", UnitType.PACKAGE, 1.99, "EUR");
        Item mango = new Item("B", "Mango", UnitType.PIECE, 1.00, "EUR");
        Item juice = new Item("C", "Potatos", UnitType.KG, 1.19, "EUR");

        Set<Item> itemSet = Sets.newHashSet(apples, mango, juice);

        Checkout checkout = Checkout.create(itemSet);
        checkout.scan("D");
    }

    @org.junit.Test
    public void test_scan_many_articles_with_discount() {
        Item apples = new Item("A", "Apple", UnitType.PACKAGE, 0.80, "EUR");
        Item mango = new Item("B", "Mango", UnitType.PIECE, 1.00, "EUR");
        Item juice = new Item("C", "Saftflasche", UnitType.PIECE, 1.20, "EUR");

        apples.addAction(new BuyManyAndSomeFreeDiscount(3, 1));
        juice.addAction(new FixPriceDiscount(Money.of(new BigDecimal(5.00), Checkout.CURRENCY), 5));

        Set<Item> itemSet = Sets.newHashSet(apples, mango, juice);

        Checkout checkout = Checkout.create(itemSet);
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("B");
        checkout.scan("B");
        checkout.scan("C");
        checkout.scan("C");
        checkout.scan("C");
        checkout.scan("C");
        checkout.scan("C");

        assertEquals(Money.of(new BigDecimal(8.6000).setScale(2, RoundingMode.HALF_EVEN), Checkout.CURRENCY),
                Money.of(BigDecimal.valueOf(checkout.total().getNumber().doubleValue()).setScale(2, RoundingMode.HALF_EVEN), Checkout.CURRENCY));
    }

    @org.junit.Test
    public void test_scan_many_articles_without_discount() {
        Item apples = new Item("A", "Apple", UnitType.PACKAGE, 1.99, "EUR");
        Item mango = new Item("B", "Mango", UnitType.PIECE, 1.00, "EUR");
        Item juice = new Item("C", "Apple in kg", UnitType.KG, 1.19, "EUR");

        Set<Item> itemSet = Sets.newHashSet(apples, mango, juice);

        Checkout checkout = Checkout.create(itemSet);
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("B");

//        Money expectedTotal = Money.of(BigDecimal.ZERO, Checkout.CURRENCY);
//        expectedTotal = expectedTotal.add(apples.getPrice()).abs();
//        expectedTotal = expectedTotal.add(apples.getPrice());

        Assert.assertEquals(Money.of(4.98, Checkout.CURRENCY), checkout.total());
    }

    @org.junit.Test
    public void test_scan_many_articles_with_multiple_discounts() {
        Item apples = new Item("A", "Apple", UnitType.PACKAGE, 1.99, "EUR");
        Item mango = new Item("B", "Mango", UnitType.PIECE, 1.00, "EUR");
        Item juice = new Item("C", "Orangensaft", UnitType.PIECE, 1.19, "EUR");

        apples.addAction(new BuyManyAndSomeFreeDiscount(3, 1));
        apples.addAction(new BuyManyAndSomeFreeDiscount(5, 2));

        Set<Item> itemSet = Sets.newHashSet(apples, mango, juice);

        Checkout checkout = Checkout.create(itemSet);
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");

        assertEquals(Money.of(new BigDecimal(5.97).setScale(2, RoundingMode.HALF_EVEN), Checkout.CURRENCY),
                Money.of(BigDecimal.valueOf(checkout.total().getNumber().doubleValue()).setScale(2, RoundingMode.HALF_EVEN), Checkout.CURRENCY));
    }
}