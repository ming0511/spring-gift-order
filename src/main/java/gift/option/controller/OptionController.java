package gift.option.controller;

import gift.option.dto.OptionCreateCommand;
import gift.option.dto.OptionCreateRequestDto;
import gift.option.dto.OptionCreateResponseDto;
import gift.option.dto.OptionGetResponseDto;
import gift.option.dto.OptionUpdateCommand;
import gift.option.dto.OptionUpdateRequestDto;
import gift.option.entity.Option;
import gift.option.entity.OptionName;
import gift.option.service.OptionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping("/{productId}/options")
    public ResponseEntity<OptionCreateResponseDto> addProductOption(
        @PathVariable Long productId,
        @Valid @RequestBody OptionCreateRequestDto requestDto) {

        OptionName optionName = new OptionName(requestDto.name());

        OptionCreateCommand dto = new OptionCreateCommand(optionName, requestDto.quantity());

        Option option = optionService.addProductOption(productId, dto);

        OptionCreateResponseDto responseDto = new OptionCreateResponseDto(
            option.getOptionId(),
            option.getName().toString(),
            option.getQuantity()
        );

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{productId}/options")
    public ResponseEntity<List<OptionGetResponseDto>> getProductOptions(
        @PathVariable Long productId
    ) {
        Set<Option> options = optionService.getProductOptions(productId);

        List<OptionGetResponseDto> responseDto = options.stream()
            .map(option -> new OptionGetResponseDto(
                option.getOptionId(),
                option.getName().toString(),
                option.getQuantity()))
            .collect(Collectors.toList());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{productId}/options/{optionId}")
    public ResponseEntity<Void> updateProductOption(
        @PathVariable Long productId,
        @PathVariable Long optionId,
        @Valid @RequestBody OptionUpdateRequestDto requestDto) {

        OptionName optionName = new OptionName(requestDto.name());

        OptionUpdateCommand dto = new OptionUpdateCommand(optionId, optionName,
            requestDto.quantity());

        optionService.updateProductOption(productId, dto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}/options/{optionId}")
    public ResponseEntity<Void> deleteProductOption(
        @PathVariable Long productId,
        @PathVariable Long optionId) {

        optionService.deleteProductOption(productId, optionId);

        return ResponseEntity.noContent().build();
    }
}
