package ing.store_management.controller;

import ing.store_management.model.dto.OrderDto;
import ing.store_management.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/placeOrder/{cartId}")
    public OrderDto placeOrder(@PathVariable Long cartId) {
        return orderService.placeOrder(cartId);
    }
}