package gift.member.service;

import gift.exception.member.LoginFailedException;
import gift.member.dto.LoginCommand;
import gift.member.dto.TokenResponseDto;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.member.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    private final MemberRepository memberRepository;

    public LoginServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public TokenResponseDto login(LoginCommand dto) {
        // DB 조회 -> 성공 시 Token 생성, 실패 시 로그인 실패
        Member member = memberRepository.findByEmail(dto.email())
            .orElseThrow(() -> new LoginFailedException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!member.getPassword().equals(dto.password())) {
            // 실패 시 로그인 실패(403 Forbidden)
            throw new LoginFailedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 성공 시 Token 생성 후 반환
        String token = new JwtTokenProvider().generateToken(
            member.getMemberId(),
            member.getName(),
            member.getRole());

        return new TokenResponseDto(token);
    }
}
