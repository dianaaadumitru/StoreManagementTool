package ing.store_management.service;

import ing.store_management.exception.CartException;
import ing.store_management.model.dto.OrderDto;
import ing.store_management.model.entity.Cart;
import ing.store_management.model.entity.CartItem;
import ing.store_management.model.entity.Order;
import ing.store_management.model.entity.OrderItem;
import ing.store_management.repository.OrderItemRepository;
import ing.store_management.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final CartService cartService;

    @Transactional
    public OrderDto placeOrder(Long cartId) {
        Cart cart = cartService.getCartById(cartId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new CartException("Cart is empty");
        }

        // Create a new order
        AtomicReference<Double> total = new AtomicReference<>((double) 0);
        cart.getItems().forEach(cartItem -> total.updateAndGet(v -> (v + cartItem.getQuantity() * cartItem.getProduct().getPrice())));
        Order order = Order.builder()
                .user(cart.getUser())
                .total(total.get())
                .build();
        order = orderRepository.save(order);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        }

        cart.getItems().clear();

        return OrderDto.builder()
                .username(order.getUser().getUsername())
                .total(order.getTotal())
                .build();
    }
}
