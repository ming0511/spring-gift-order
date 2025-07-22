package gift.option.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record OptionCreateRequestDto(
    @NotNull(message = "옵션 이름은 필수 입력 항목입니다.")
    @Length(max = 50, message = "옵션 이름은 공백을 포함하여 최대 50자까지 입력할 수 있습니다.")
    @Pattern(
        regexp = "^[\\p{L}\\p{N} ()\\[\\]+\\-&/_]*$",
        message = "옵션 이름에는 문자, 숫자가 포함되어 있으며 다음과 같은 특수 문자가 허용됩니다: ( ) [ ] + - & / _"
    )
    String name,

    @NotNull(message = "옵션 수량은 필수 입력 항목입니다.")
    @Min(value = 1, message = "옵션 수량은 최소 1개 이상이어야 합니다.")
    @Max(value = 100_000_000 - 1, message = "옵션 수량은 1억 미만이어야 합니다.")
    Integer quantity
) {

}
