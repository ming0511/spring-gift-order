package gift.exception;

import gift.exception.member.EmailAlreadyExistsException;
import gift.exception.member.LoginFailedException;
import gift.exception.member.MemberNotFoundException;
import gift.exception.option.DuplicateOptionNameException;
import gift.exception.option.OptionNotFoundException;
import gift.exception.product.ProductMismatchException;
import gift.exception.product.ProductNotFoundException;
import gift.exception.product.UnapprovedProductException;
import gift.exception.wish.InvalidAuthorizationException;
import gift.exception.wish.InvalidPageException;
import gift.exception.wish.InvalidTokenException;
import gift.exception.wish.WishNotFoundException;
import gift.exception.wish.WishlistAccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    // 404 리소스 없음
    @ExceptionHandler({ProductNotFoundException.class, MemberNotFoundException.class,
        WishNotFoundException.class, OptionNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(RuntimeException ex) {
        return new ResponseEntity<>("오류: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 400
    // 400 유효성 검사 실패 - 협의되지 않은 '카카오', 이미 존재하는 이메일, 페이지 설정
    @ExceptionHandler({UnapprovedProductException.class, EmailAlreadyExistsException.class,
        InvalidPageException.class})
    public ResponseEntity<String> handleBadRequestException(RuntimeException ex) {
        return new ResponseEntity<>("오류: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 400 입력 값 검증 실패 - 옵션 이름 중복, 잘못된 옵션 ID
    @ExceptionHandler({DuplicateOptionNameException.class, ProductMismatchException.class})
    public ResponseEntity<String> handleDuplicateOptionNameException(RuntimeException ex) {
        return new ResponseEntity<>("오류: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 401 인증 실패
    @ExceptionHandler({InvalidAuthorizationException.class, InvalidTokenException.class})
    public ResponseEntity<String> handleUnauthorizedException(RuntimeException ex) {
        return new ResponseEntity<>("오류: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 403
    // 403 권한 없음 - 잘못된 로그인
    // (비밀번호 찾기, 비밀번호 변경 요청)
    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<String> handleLoginForbiddenException(RuntimeException ex) {
        return new ResponseEntity<>("오류: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // 403 권한 없음
    @ExceptionHandler(WishlistAccessDeniedException.class)
    public ResponseEntity<String> handleForbiddenException(RuntimeException ex) {
        return new ResponseEntity<>("오류: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
