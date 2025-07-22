package gift.member.dto;

public record LoginCommand(
    String email,
    String password
) {

}
