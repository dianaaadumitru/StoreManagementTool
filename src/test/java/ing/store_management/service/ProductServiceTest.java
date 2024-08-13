package ing.store_management.service;

import ing.store_management.model.dto.ProductDto;
import ing.store_management.model.entity.Product;
import ing.store_management.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ing.store_management.service.ProductService.PRODUCT_DOES_NOT_EXIST_CONSTANT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    private ProductDto productDto;

    @BeforeEach
    void initData() {
        MockitoAnnotations.openMocks(this);

        product = Product.builder()
                .id(1L)
                .name("carrots")
                .stock(300)
                .price(1.5)
                .build();

        productDto = ProductDto.builder()
                .price(1.5)
                .name("carrots")
                .stock(300)
                .id(1L)
                .build();
    }

    @Test
    void validProduct_addProduct_productAdded() {
        when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(product);

        //act
        ProductDto created = productService.addProduct(productDto);

        //assert
        assertEquals(created, productDto);
        verify(productRepository).save(ArgumentMatchers.any(Product.class));
    }

    @Test
    void givenProduct_getProductById_returnSameProduct() {
        //arrange
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        //when
        ProductDto expectedDto = productService.getProductById(product.getId());
        ProductDto actualDto = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .price(product.getPrice())
                .build();

        //then
        assertEquals(expectedDto, actualDto);
        verify(productRepository).findById(product.getId());
    }

    @Test
    void givenProduct_getProductById_productNotFound() {
        //arrange
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        //act and assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.getProductById(product.getId()));
        assertEquals(PRODUCT_DOES_NOT_EXIST_CONSTANT, exception.getMessage());
    }

    @Test
    void getAllProducts_productRetrieved() {
        //arrange
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(productRepository.findAll()).thenReturn(products);

        //when
        List<ProductDto> expected = productService.getAllProducts();
        List<ProductDto> productDtos = products.stream().map(product -> ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .price(product.getPrice())
                .build()).collect(Collectors.toList());

        //then
        assertEquals(expected, productDtos);
        verify(productRepository).findAll();
    }

    @Test
    void newProductGiven_updateProduct_productUpdated() {
        //arrange
        ProductDto expectedProduct = ProductDto.builder()
                .id(1L)
                .name("apples")
                .stock(300)
                .price(1.5)
                .build();

        when(productRepository.findById(expectedProduct.getId())).thenReturn(Optional.of(product));

        //act
        ProductDto actualProduct = productService.updateProduct(productDto.getId(), expectedProduct);

        //assert
        assertEquals(expectedProduct, actualProduct);
        verify(productRepository).findById(product.getId());
    }

    @Test
    void productThatDoesntExistGiven_updateProduct_productUpdated() {
        //arrange
        ProductDto newProduct = ProductDto.builder()
                .id(1L)
                .name("apples")
                .stock(300)
                .price(1.5)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        //act and assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.updateProduct(product.getId(), newProduct));
        assertEquals(PRODUCT_DOES_NOT_EXIST_CONSTANT, exception.getMessage());
    }

    @Test
    void givenProduct_removeProduct_productRemoved() {
        //arrange
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        //act
        productService.removeProduct(product.getId());

        //assert
        verify(productRepository).deleteById(product.getId());
    }

    @Test
    void givenProduct_removeProduct_productDoesNotExist() {
        //arrange
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        //act and assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.removeProduct(product.getId()));
        assertEquals(PRODUCT_DOES_NOT_EXIST_CONSTANT, exception.getMessage());
    }

    @Test
    void givenPrefix_getProductsByNameStartingWith_productsRetrieved() {
        // arrange
        List<Product> products = new ArrayList<>();
        products.add(product);
        String prefix = "car";

        when(productRepository.findProductsByNameStartingWith(prefix)).thenReturn(products);

        // act
        List<ProductDto> result = productService.getProductsByNameStartingWith(prefix);

        // assert
        List<ProductDto> expected = new ArrayList<>();
        expected.add(productDto);

        assertEquals(expected, result);
        verify(productRepository).findProductsByNameStartingWith(prefix);
    }

    @Test
    void givenNonMatchingPrefix_getProductsByNameStartingWith_noProductsRetrieved() {
        // arrange
        String prefix = "xyz";
        when(productRepository.findProductsByNameStartingWith(prefix)).thenReturn(new ArrayList<>());

        // act
        List<ProductDto> result = productService.getProductsByNameStartingWith(prefix);

        // assert
        assertEquals(0, result.size());
        verify(productRepository).findProductsByNameStartingWith(prefix);
    }

    @Test
    void givenEmptyPrefix_getProductsByNameStartingWith_noProductsRetrieved() {
        // arrange
        String prefix = "";
        when(productRepository.findProductsByNameStartingWith(prefix)).thenReturn(new ArrayList<>());

        // act
        List<ProductDto> result = productService.getProductsByNameStartingWith(prefix);

        // assert
        assertEquals(0, result.size());
        verify(productRepository).findProductsByNameStartingWith(prefix);
    }

    @Test
    void givenNullPrefix_getProductsByNameStartingWith_noProductsRetrieved() {
        // arrange
        String prefix = null;
        when(productRepository.findProductsByNameStartingWith(prefix)).thenReturn(new ArrayList<>());

        // act
        List<ProductDto> result = productService.getProductsByNameStartingWith(prefix);

        // assert
        assertEquals(0, result.size());
        verify(productRepository).findProductsByNameStartingWith(prefix);
    }

    @Test
    void givenCaseInsensitivePrefix_getProductsByNameStartingWith_productsRetrieved() {
        // arrange
        List<Product> products = new ArrayList<>();
        products.add(product);
        String prefix = "Car";

        when(productRepository.findProductsByNameStartingWith(prefix.toLowerCase())).thenReturn(products);

        // act
        List<ProductDto> result = productService.getProductsByNameStartingWith(prefix);

        // assert
        List<ProductDto> expected = new ArrayList<>();
        expected.add(productDto);

        assertEquals(expected, result);
        verify(productRepository).findProductsByNameStartingWith(prefix.toLowerCase());
    }
}