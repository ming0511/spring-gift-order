package gift.member.dto;

import gift.member.Role;

public record AdminMemberGetResponseDto(
    Long memberId,
    String email,
    String password,
    String name,
    Role role
) {

}
