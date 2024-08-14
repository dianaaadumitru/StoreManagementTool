package ing.store_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import ing.store_management.exception.CartException;
import ing.store_management.exception.ProductException;
import ing.store_management.exception.UserException;
import ing.store_management.model.dto.CartItemDto;
import ing.store_management.model.entity.Cart;
import ing.store_management.model.entity.CartItem;
import ing.store_management.model.entity.Product;
import ing.store_management.model.entity.User;
import ing.store_management.repository.CartItemRepository;
import ing.store_management.repository.CartRepository;
import ing.store_management.repository.ProductRepository;
import ing.store_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private Product product;
    private User user;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void initData() {
        MockitoAnnotations.openMocks(this);

        product = Product.builder()
                .id(1L)
                .name("carrots")
                .stock(300)
                .price(1.5)
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .addedAt(LocalDateTime.now().minusMinutes(15))
                .build();

        cart.setItems(new HashSet<>(Collections.singletonList(cartItem)));
    }

    @Test
    void addProductToCart_validProductAndUser_cartItemAdded() {
        //arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(cart);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        //act
        cartService.addProductToCart(user.getId(), product.getId(), 5);

        //assert
        verify(cartItemRepository).save(any(CartItem.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addProductToCart_productDoesNotExist_throwsException() {
        //arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act and assert
        Exception exception = assertThrows(ProductException.class, () -> cartService.addProductToCart(user.getId(), product.getId(), 5));
        assertEquals("Product does not exist", exception.getMessage());
    }

    @Test
    void addProductToCart_userDoesNotExist_throwsException() {
        //arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act and assert
        Exception exception = assertThrows(UserException.class, () -> cartService.addProductToCart(user.getId(), product.getId(), 5));
        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void checkCartItems_oldCartItem_cartItemRemoved() {
        //arange
        cartItem.setAddedAt(LocalDateTime.now().minusMinutes(40));
        when(cartItemRepository.findAll()).thenReturn(Collections.singletonList(cartItem));

        //act and assert
        cartService.checkCartItems();

        verify(cartItemRepository).delete(any(CartItem.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void removeProductFromCart_productExists_cartItemRemoved() {
        //arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(cart);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        //act and assert
        cartService.removeProductFromCart(user.getId(), product.getId());

        verify(cartItemRepository).delete(any(CartItem.class));
        verify(productRepository).save(any(Product.class));
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void removeProductFromCart_productNotInCart_nothingRemoved() {
        //arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(cart);

        cart.getItems().clear();

        //act and assert
        cartService.removeProductFromCart(user.getId(), product.getId());

        verify(cartItemRepository, never()).delete(any(CartItem.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void removeProductFromCart_cartDoesNotExist_throwsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(null);

        Exception exception = assertThrows(CartException.class, () -> cartService.removeProductFromCart(user.getId(), product.getId()));
        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void removeProductFromCart_userDoesNotExist_throwsException() {
        //arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act and assert
        Exception exception = assertThrows(UserException.class, () -> cartService.removeProductFromCart(user.getId(), product.getId()));
        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void getAllCartItems_userWithCart_returnsCartItems() {
        //arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(cart);

        //act
        List<CartItemDto> cartItems = cartService.getAllCartItems(user.getId());

        //assert
        assertEquals(1, cartItems.size());
        assertEquals(product.getName(), cartItems.get(0).getProductName());
        verify(cartRepository).findByUser(any(User.class));
    }

    @Test
    void getAllCartItems_cartDoesNotExist_throwsException() {
        //arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(any(User.class))).thenReturn(null);

        //act and assert
        Exception exception = assertThrows(CartException.class, () -> cartService.getAllCartItems(user.getId()));
        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void getAllCartItems_userDoesNotExist_throwsException() {
        //arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act and assert
        Exception exception = assertThrows(UserException.class, () -> cartService.getAllCartItems(user.getId()));
        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void getCartById_cartExists_returnsCart() {
        //arrange
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));

        //act
        Cart result = cartService.getCartById(cart.getId());

        //assert
        assertEquals(cart, result);
        verify(cartRepository).findById(anyLong());
    }

    @Test
    void getCartById_cartDoesNotExist_throwsException() {
        //arrange
        when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act and assert
        Exception exception = assertThrows(CartException.class, () -> cartService.getCartById(cart.getId()));
        assertEquals("Cart does not exist", exception.getMessage());
    }
}
