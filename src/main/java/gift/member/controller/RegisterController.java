package gift.member.controller;

import gift.member.dto.RegisterCommand;
import gift.member.dto.RegisterRequestDto;
import gift.member.dto.TokenResponseDto;
import gift.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class RegisterController {

    private final MemberService memberService;

    public RegisterController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> registerMember(
        @Valid @RequestBody RegisterRequestDto requestDto) {

        RegisterCommand dto = new RegisterCommand(requestDto.email(), requestDto.password(),
            requestDto.name(), requestDto.role());

        TokenResponseDto responseDto = memberService.registerMember(dto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
