package ing.store_management.service;

import ing.store_management.exception.CartException;
import ing.store_management.exception.ProductException;
import ing.store_management.exception.UserException;
import ing.store_management.model.dto.CartItemDto;
import ing.store_management.model.dto.ProductDto;
import ing.store_management.model.entity.*;
import ing.store_management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class CartService {
    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductRepository productRepository;

    private final CustomerRepository customerRepository;

    @Transactional
    public void addProductToCart(Long customerId, Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductException("Product does not exist"));

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new UserException("User does not exist"));
        Cart cart = cartRepository.findByCustomer(customer);
        if (cart == null) {
            cart = Cart.builder()
                    .customer(customer)
                    .build();
            cart = cartRepository.save(cart);
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setAddedAt(LocalDateTime.now());

        cartItemRepository.save(cartItem);

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Scheduled(fixedRate = 60000)
    public void checkCartItems() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusMinutes(30);

        Iterable<CartItem> cartItems = cartItemRepository.findAll();
        cartItems.forEach(cartItem -> {
            if (cartItem.getAddedAt().isBefore(threshold)) {
                Product product = cartItem.getProduct();
                product.setStock(product.getStock() + cartItem.getQuantity());
                productRepository.save(product);
                cartItemRepository.delete(cartItem);
            }
        });
    }

    @Transactional
    public void removeProductFromCart(Long customerId, Long productId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new UserException("User does not exist"));
        Cart cart = cartRepository.findByCustomer(customer);
        if (cart == null) {
            throw new CartException("Cart not found");
        }

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() + cartItem.getQuantity());
            productRepository.save(product);
            cartItemRepository.delete(cartItem);
        }
    }

    public List<CartItemDto> getAllCartItems(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new CartException("Cart does not exist"));
        Set<CartItem> cartItems = cart.getItems();
        List<CartItemDto> cartItemDtos = new ArrayList<>();

        cartItems.forEach(cartItem ->
                cartItemDtos.add(CartItemDto.builder()
                                .productName(cartItem.getProduct().getName())
                                .productPrice(cartItem.getProduct().getPrice())
                                .quantity(cartItem.getQuantity())
                        .build()));

        return cartItemDtos;
    }
}
