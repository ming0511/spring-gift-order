package gift.member.dto;

import gift.member.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminMemberCreateRequestDto(
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    String password,

    @NotNull(message = "회원 이름은 필수 입력 항목입니다.")
    String name,

    Role role
) {

}
