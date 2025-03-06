package org.example;

import org.example.Amazon.*;
import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AmazonUnitTest {
    private ShoppingCart mockShoppingCart;
    private List<PriceRule> mockRules;
    private Amazon amazon;

    @BeforeEach
    void setUp() {
        mockShoppingCart = Mockito.mock(ShoppingCart.class);
        mockRules = new ArrayList<>();
        amazon = new Amazon(mockShoppingCart, mockRules);
    }

    @Test
    @DisplayName("Specification-based: Get total cost with no price rules")
    void testCostWithoutRules() {
        when(mockShoppingCart.getItems()).thenReturn(new ArrayList<>());
        double total = amazon.calculate();
        assertEquals(0, total, "Total price should be 0 when no rules are applied.");
    }

    @Test
    @DisplayName("Specification-based: Add item to cart")
    void testAddToCart() {
        Item item = new Item(ItemType.OTHER, "Book", 1, 10.0);
        amazon.addToCart(item);
        verify(mockShoppingCart, times(1)).add(item);
    }

    @Test
    @DisplayName("Structural-based: Ensures calculate method applies price rules")
    void testCalculateAppliesPriceRules() {
        PriceRule rule1 = Mockito.mock(PriceRule.class);
        PriceRule rule2 = Mockito.mock(PriceRule.class);
        mockRules.add(rule1);
        mockRules.add(rule2);

        when(rule1.priceToAggregate(anyList())).thenReturn(10.0);
        when(rule2.priceToAggregate(anyList())).thenReturn(5.0);

        double total = amazon.calculate();

        assertEquals(15.0, total, "Total should be sum of all price rules.");
    }
}
