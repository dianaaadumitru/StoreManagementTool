package ing.store_management.service;

import ing.store_management.exception.ProductException;
import ing.store_management.model.dto.ProductDto;
import ing.store_management.model.entity.Product;
import ing.store_management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductDto addProduct(ProductDto newProduct) {
        Product product = productRepository.save(Product.builder()
                .name(newProduct.getName())
                .price(newProduct.getPrice())
                .stock(newProduct.getStock())
                .build());

        newProduct.setId(product.getId());
        return newProduct;
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductException("Product does not exist"));

        return ProductDto.builder()
                .id(id)
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    public List<ProductDto> getAllProducts() {
        Iterable<Product> productsList = productRepository.findAll();
        List<ProductDto> productDtos = new ArrayList<>();

        productsList.forEach(product ->
                productDtos.add(ProductDto.builder()
                        .id(product.getId())
                        .price(product.getPrice())
                        .name(product.getName())
                        .stock(product.getStock())
                        .build()));
        return productDtos;
    }

    public ProductDto updateProduct(Long id, ProductDto updatedProduct) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductException("Product does not exist"));

        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setStock(updatedProduct.getStock());
        productRepository.save(product);

        updatedProduct.setId(product.getId());
        return updatedProduct;
    }

    public void removeProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductException("Product does not exist"));

        productRepository.deleteById(id);
    }
}
