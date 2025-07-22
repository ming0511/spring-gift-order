package gift.member.dto;

import gift.member.Role;

public record MemberUpdateCommand(
    String email,
    String password,
    String name,
    Role role
) {

}
