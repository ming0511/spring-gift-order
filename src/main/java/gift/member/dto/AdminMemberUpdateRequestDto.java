package gift.member.dto;

import gift.member.Role;

public record AdminMemberUpdateRequestDto(
    String email,
    String password,
    String name,
    Role role
) {

}
