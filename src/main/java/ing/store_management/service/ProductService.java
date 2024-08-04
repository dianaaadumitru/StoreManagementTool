package ing.store_management.service;

import ing.store_management.model.dto.ProductDto;
import ing.store_management.model.entity.Product;
import ing.store_management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductDto addProduct(ProductDto newProduct) {
        Product product = productRepository.save(Product.builder()
                        .name(newProduct.getName())
                        .price(newProduct.getPrice())
                .build());

        newProduct.setId(product.getId());
        return newProduct;
    }
}
