package ing.store_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import ing.store_management.exception.CartException;
import ing.store_management.model.dto.OrderDto;
import ing.store_management.model.entity.*;
import ing.store_management.repository.OrderItemRepository;
import ing.store_management.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    private Cart cart;
    private Order order;
    private CartItem cartItem;
    private Product product;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = Product.builder()
                .id(1L)
                .name("Product A")
                .price(10.0)
                .stock(100)
                .build();

        user = User.builder()
                .id(1L)
                .username("user1")
                .build();

        cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(cartItems)
                .build();

        order = Order.builder()
                .id(1L)
                .user(user)
                .total(20.0)
                .build();
    }

    @Test
    void placeOrder_cartExistsAndNotEmpty_orderPlacedSuccessfully() {
        //arrange
        when(cartService.getCartById(anyLong())).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //act
        OrderDto orderDto = orderService.placeOrder(cart.getId());

        //assert
        assertNotNull(orderDto);
        assertEquals("user1", orderDto.getUsername());
        assertEquals(20.0, orderDto.getTotal());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        assertTrue(cart.getItems().isEmpty(), "Cart should be empty after placing order");
    }

    @Test
    void placeOrder_cartDoesNotExist_throwsCartException() {
        //arrange
        when(cartService.getCartById(anyLong())).thenReturn(null);

        //act and assert
        Exception exception = assertThrows(CartException.class, () -> orderService.placeOrder(1L));

        assertEquals("Cart is empty", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    void placeOrder_cartIsEmpty_throwsCartException() {
        //arrange
        cart.getItems().clear();
        when(cartService.getCartById(anyLong())).thenReturn(cart);

        //act and assert
        Exception exception = assertThrows(CartException.class, () -> orderService.placeOrder(1L));

        assertEquals("Cart is empty", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    void placeOrder_multipleItems_orderPlacedSuccessfully() {
        //arrange
        CartItem anotherCartItem = CartItem.builder()
                .id(2L)
                .product(product)
                .quantity(3)
                .build();

        cart.getItems().add(anotherCartItem);

        when(cartService.getCartById(anyLong())).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //act
        OrderDto orderDto = orderService.placeOrder(cart.getId());

        //assert
        assertNotNull(orderDto);
        assertEquals("user1", orderDto.getUsername());
        assertEquals(20.0, orderDto.getTotal());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
        assertTrue(cart.getItems().isEmpty(), "Cart should be empty after placing order");
    }
}
