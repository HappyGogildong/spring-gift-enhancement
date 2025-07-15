package gift.service;

import gift.dto.request.LoginRequestDto;
import gift.dto.request.RegisterRequestDto;
import gift.dto.response.TokenResponseDto;
import gift.entity.Member;
import java.util.Optional;

public interface MemberService {

    TokenResponseDto registerAndReturnToken(RegisterRequestDto registerRequestDto);

    TokenResponseDto login(LoginRequestDto loginRequest);

    Member memberWithEncodedPassword(RegisterRequestDto registerRequestDto);

}
