package gift.member.dto;

import gift.member.Role;

public record MemberCreateCommand(
    String email,
    String password,
    String name,
    Role role) {

}
