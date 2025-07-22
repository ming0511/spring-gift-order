package gift.member.controller;

import gift.member.dto.LoginCommand;
import gift.member.dto.LoginRequestDto;
import gift.member.dto.TokenResponseDto;
import gift.member.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService authService) {
        this.loginService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
        @Valid @RequestBody LoginRequestDto requestDto) {

        LoginCommand dto = new LoginCommand(requestDto.email(), requestDto.password());

        TokenResponseDto responseDto = loginService.login(dto);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
