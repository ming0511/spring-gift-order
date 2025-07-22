package gift.product.controller;

import gift.option.dto.OptionCreateCommand;
import gift.option.entity.OptionName;
import gift.product.dto.ProductCreateCommand;
import gift.product.dto.ProductCreateRequestDto;
import gift.product.dto.ProductGetResponseDto;
import gift.product.dto.ProductPageResponseDto;
import gift.product.dto.ProductUpdateCommand;
import gift.product.dto.ProductUpdateRequestDto;
import gift.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/create")
    public String createProductPage() {
        return "product/create-product";
    }

    @PostMapping("/create")
    public String createProduct(
        @Valid @ModelAttribute ProductCreateRequestDto requestDto,
        BindingResult bindingResult, Model model
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "product/create-product";
        }

        Set<OptionCreateCommand> options = requestDto.options().stream()
            .map(option -> {
                OptionName optionName = new OptionName(option.name());
                return new OptionCreateCommand(optionName, option.quantity());
            })
            .collect(Collectors.toSet());

        ProductCreateCommand dto = new ProductCreateCommand(requestDto.name(), requestDto.price(),
            requestDto.imageUrl(), requestDto.mdConfirmed(), options);

        try {
            productService.saveProduct(dto);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "product/create-product";
        }
    }

    @GetMapping
    public String getProductsPage(
        @PageableDefault(sort = "productId", direction = Sort.Direction.DESC) Pageable pageable,
        Model model) {

        ProductPageResponseDto products = productService.findAllProducts(pageable);
        model.addAttribute("products", products);
        return "product/products";
    }

    @GetMapping("/search")
    public String getProductById(@RequestParam Long productId, Model model) {

        try {
            ProductGetResponseDto product = productService.findProductById(productId);
            model.addAttribute("products", List.of(product));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "product/products";
    }

    @GetMapping("/update/{productId}")
    public String updateProductPage(@PathVariable Long productId, Model model) {

        try {
            ProductGetResponseDto product = productService.findProductById(productId);
            model.addAttribute("product", product);
            return "product/update-product";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/update/{productId}")
    public String updateProductById(
        @PathVariable Long productId,
        @Valid @ModelAttribute ProductUpdateRequestDto requestDto,
        BindingResult bindingResult, RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("productUpdateRequestDto",
                requestDto);
            return "redirect:/admin/products/update/" + productId;
        }

        ProductUpdateCommand dto = new ProductUpdateCommand(requestDto.name(), requestDto.price(),
            requestDto.imageUrl(), requestDto.mdConfirmed());

        try {
            productService.updateProduct(productId, dto);
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            return "redirect:/admin/products/update/" + productId;
        }
    }

    @PostMapping("/delete/{productId}")
    public String deleteProductById(@PathVariable Long productId, Model model) {

        try {
            productService.deleteProduct(productId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/products";
    }
}