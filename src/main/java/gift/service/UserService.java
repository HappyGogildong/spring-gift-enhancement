package gift.service;

import gift.dto.request.LoginRequestDto;
import gift.dto.request.RegisterRequestDto;
import gift.dto.response.TokenResponseDto;
import gift.entity.Member;
import java.util.Optional;

public interface UserService {

    TokenResponseDto registerAndReturnToken(RegisterRequestDto registerRequestDto);

    TokenResponseDto login(LoginRequestDto loginRequest);

    Member userWithEncodedPassword(RegisterRequestDto registerRequestDto);

    Optional<Member> getUserByEmail(String email);

}
