package org.example;
import org.example.Barnes.*;
import org.example.Barnes.BarnesAndNoble;
import org.example.Barnes.BuyBookProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class BarnesAndNobleTest {
    private BookDatabase mockDatabase;
    private BuyBookProcess mockProcess;
    private BarnesAndNoble barnesAndNoble;

    @BeforeEach
    void setUp() {
        mockDatabase = Mockito.mock(BookDatabase.class);
        mockProcess = Mockito.mock(BuyBookProcess.class);
        barnesAndNoble = new BarnesAndNoble(mockDatabase, mockProcess);
    }

    @Test
    @DisplayName("Specification-based: Get total price for books in cart")
    void testGetPriceForCart() {
        Book book1 = new Book("123", 10, 5);
        Book book2 = new Book("456", 20, 2);

        when(mockDatabase.findByISBN("123")).thenReturn(book1);
        when(mockDatabase.findByISBN("456")).thenReturn(book2);

        Map<String, Integer> order = new HashMap<>();
        order.put("123", 2);
        order.put("456", 1);

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertNotNull(summary);
        assertEquals(40, summary.getTotalPrice());
    }

    @Test
    @DisplayName("Specification-based: Handle null order")
    void testNullOrder() {
        assertNull(barnesAndNoble.getPriceForCart(null));
    }

    @Test
    @DisplayName("Specification-based: Handle book with insufficient stock")
    void testInsufficientStock() {
        Book book = new Book("789", 15, 1);
        when(mockDatabase.findByISBN("789")).thenReturn(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("789", 3);

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertNotNull(summary);
        assertEquals(15, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().containsKey(book));
        assertEquals(2, summary.getUnavailable().get(book));
    }

    @Test
    @DisplayName("Structural-based: Verify retrieveBook interaction with BuyBookProcess")
    void testRetrieveBookInteraction() {
        Book book = new Book("321", 30, 4);
        when(mockDatabase.findByISBN("321")).thenReturn(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("321", 2);

        barnesAndNoble.getPriceForCart(order);

        verify(mockProcess, times(1)).buyBook(book, 2);
    }

    @Test
    @DisplayName("Structural-based: Verify retrieveBook handles unavailable books")
    void testRetrieveBookHandlesUnavailableBooks() {
        Book book = new Book("111", 50, 0);
        when(mockDatabase.findByISBN("111")).thenReturn(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("111", 1);

        PurchaseSummary summary = barnesAndNoble.getPriceForCart(order);

        assertNotNull(summary);
        assertEquals(0, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().containsKey(book));
        assertEquals(1, summary.getUnavailable().get(book));
    }
}
