package ing.store_management.controller;

import ing.store_management.model.dto.CartItemDto;
import ing.store_management.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Void> addProductToCart(@RequestParam Long customerId, @RequestParam Long productId, @RequestParam int quantity) {
        cartService.addProductToCart(customerId, productId, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeProductFromCart(@RequestParam Long customerId, @RequestParam Long productId) {
        cartService.removeProductFromCart(customerId, productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/all/{cartId}")
    public ResponseEntity<List<CartItemDto>> getAllItems(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.getAllCartItems(cartId));
    }
}
