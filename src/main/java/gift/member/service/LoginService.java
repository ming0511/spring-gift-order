package gift.member.service;

import gift.member.dto.LoginCommand;
import gift.member.dto.TokenResponseDto;

public interface LoginService {

    TokenResponseDto login(LoginCommand dto);
}
