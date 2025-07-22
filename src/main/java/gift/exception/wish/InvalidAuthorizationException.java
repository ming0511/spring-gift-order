package gift.exception.wish;

public class InvalidAuthorizationException extends RuntimeException {

    public InvalidAuthorizationException() {
        super("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
    }

    public InvalidAuthorizationException(String message) {
        super(message);
    }
}
