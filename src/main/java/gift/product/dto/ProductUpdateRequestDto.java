package gift.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequestDto(
    @NotNull(message = "상품명은 필수 입력 항목입니다.")
    @Size(max = 15, message = "상품명은 최대 15자까지 입력할 수 있습니다.")
    @Pattern(
        regexp = "^[\\p{L}\\p{N} ()\\[\\]+\\-&/_]*$",
        message = "상품 이름에는 문자, 숫자가 포함되어 있으며 다음과 같은 특수 문자가 허용됩니다: ( ) [ ] + - & / _"
    )
    String name,

    @NotNull(message = "상품 가격은 필수 입력 항목입니다.")
    @Min(value = 0, message = "상품 가격은 0 이상이어야 합니다.")
    Double price,

    @NotNull(message = "상품 이미지 URL은 필수 입력 항목입니다.")
    String imageUrl,

    @NotNull(message = "상품 MD 확인 여부는 필수 입력 항목입니다.")
    Boolean mdConfirmed) {

}