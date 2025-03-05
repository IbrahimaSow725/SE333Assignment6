SOEN 333 - Assignment 6

This project focuses on writing unit and integration tests for the Amazon and BarnesAndNoble packages.

Test Cases Used in Part 1:
public class BarnesAndNobleTest { 
    private BookDatabase mockDatabase;
    private BuyBookProcess mockProcess;
    private BarnesAndNoble barnesAndNoble;

    @BeforeEach
    void setUp() {
        this.mockDatabase = (BookDatabase)Mockito.mock(BookDatabase.class);
        this.mockProcess = (BuyBookProcess)Mockito.mock(BuyBookProcess.class);
        this.barnesAndNoble = new BarnesAndNoble(this.mockDatabase, this.mockProcess);
    }

    @Test
    @DisplayName("Specification-based: Get total price for books in cart")
    void testGetPriceForCart() {
        Book book1 = new Book("123", 10, 5);
        Book book2 = new Book("456", 20, 2);
        Mockito.when(this.mockDatabase.findByISBN("123")).thenReturn(book1);
        Mockito.when(this.mockDatabase.findByISBN("456")).thenReturn(book2);
        Map<String, Integer> order = new HashMap();
        order.put("123", 2);
        order.put("456", 1);
        PurchaseSummary summary = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertNotNull(summary);
        Assertions.assertEquals(40, summary.getTotalPrice());
    }

    @Test
    @DisplayName("Specification-based: Handle null order")
    void testNullOrder() {
        Assertions.assertNull(this.barnesAndNoble.getPriceForCart((Map)null));
    }

    @Test
    @DisplayName("Specification-based: Handle book with insufficient stock")
    void testInsufficientStock() {
        Book book = new Book("789", 15, 1);
        Mockito.when(this.mockDatabase.findByISBN("789")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("789", 3);
        PurchaseSummary summary = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertNotNull(summary);
        Assertions.assertEquals(15, summary.getTotalPrice());
        Assertions.assertTrue(summary.getUnavailable().containsKey(book));
        Assertions.assertEquals(2, (Integer)summary.getUnavailable().get(book));
    }

    @Test
    @DisplayName("Structural-based: Verify retrieveBook interaction with BuyBookProcess")
    void testRetrieveBookInteraction() {
        Book book = new Book("321", 30, 4);
        Mockito.when(this.mockDatabase.findByISBN("321")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("321", 2);
        this.barnesAndNoble.getPriceForCart(order);
        ((BuyBookProcess)Mockito.verify(this.mockProcess, Mockito.times(1))).buyBook(book, 2);
    }

    @Test
    @DisplayName("Structural-based: Verify retrieveBook handles unavailable books")
    void testRetrieveBookHandlesUnavailableBooks() {
        Book book = new Book("111", 50, 0);
        Mockito.when(this.mockDatabase.findByISBN("111")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("111", 1);
        PurchaseSummary summary = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertNotNull(summary);
        Assertions.assertEquals(0, summary.getTotalPrice());
        Assertions.assertTrue(summary.getUnavailable().containsKey(book));
        Assertions.assertEquals(1, (Integer)summary.getUnavailable().get(book));
    }
}

Part 2 - Build Badge
![Build Status](https://github.com/IbrahimaSow725/SE333Assignment6/actions/workflows/SE333_CI.yml/badge.svg)