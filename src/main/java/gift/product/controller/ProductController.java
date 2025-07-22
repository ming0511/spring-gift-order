package gift.product.controller;

import gift.option.dto.OptionCreateCommand;
import gift.option.dto.OptionCreateResponseDto;
import gift.option.entity.OptionName;
import gift.product.dto.ProductCreateCommand;
import gift.product.dto.ProductCreateRequestDto;
import gift.product.dto.ProductCreateResponseDto;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductPageResponseDto;
import gift.product.dto.ProductUpdateCommand;
import gift.product.dto.ProductUpdateRequestDto;
import gift.product.entity.Product;
import gift.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping
    public ResponseEntity<ProductCreateResponseDto> createProduct(
        @Valid @RequestBody ProductCreateRequestDto requestDto) {

        Set<OptionCreateCommand> options = requestDto.options().stream()
            .map(optionDto -> {
                OptionName optionName = new OptionName(optionDto.name());
                return new OptionCreateCommand(optionName, optionDto.quantity());
            })
            .collect(Collectors.toSet());

        ProductCreateCommand dto = new ProductCreateCommand(requestDto.name(), requestDto.price(),
            requestDto.imageUrl(), requestDto.mdConfirmed(), options);

        Product product = productService.saveProduct(dto);

        List<OptionCreateResponseDto> optionResponseDtos = product.getOptions().stream()
            .map(optionDto -> new OptionCreateResponseDto(optionDto.getOptionId(),
                optionDto.getName().toString(),
                optionDto.getQuantity()))
            .collect(Collectors.toList());

        ProductCreateResponseDto responseDto = new ProductCreateResponseDto(product.getProductId(),
            product.getName(), product.getPrice(), product.getImageUrl(), product.getMdConfirmed(),
            optionResponseDtos);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ProductPageResponseDto getProducts(
        @PageableDefault(sort = "productId", direction = Sort.Direction.DESC) Pageable pageable) {

        return productService.findAllProducts(pageable);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductGetResponseDto> getProductById(@PathVariable Long productId) {

        ProductGetResponseDto responseDto = productService.findProductById(productId);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProductById(@PathVariable Long productId,
        @Valid @RequestBody ProductUpdateRequestDto requestDto) {

        ProductUpdateCommand dto = new ProductUpdateCommand(requestDto.name(), requestDto.price(),
            requestDto.imageUrl(), requestDto.mdConfirmed());

        productService.updateProduct(productId, dto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long productId) {

        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}