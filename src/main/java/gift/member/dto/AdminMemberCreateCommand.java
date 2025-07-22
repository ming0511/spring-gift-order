package gift.member.dto;

import gift.member.Role;

public record AdminMemberCreateCommand(
    String email,
    String password,
    String name,
    Role role) {

}
