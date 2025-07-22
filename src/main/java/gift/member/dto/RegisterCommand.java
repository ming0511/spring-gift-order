package gift.member.dto;

import gift.member.Role;

public record RegisterCommand(
    String email,
    String password,
    String name,
    Role role) {

}
