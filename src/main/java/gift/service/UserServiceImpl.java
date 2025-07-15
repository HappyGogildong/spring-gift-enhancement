package gift.service;


import gift.auth.JwtTokenHandler;
import gift.dto.request.LoginRequestDto;
import gift.dto.request.RegisterRequestDto;
import gift.dto.response.TokenResponseDto;
import gift.entity.Member;
import gift.exception.EmailDuplicationException;
import gift.exception.InvalidPasswordException;
import gift.exception.UserNotFoundException;
import gift.repository.MemberRepository;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final MemberRepository memberRepository;
    private final JwtTokenHandler jwtTokenHandler;


    public UserServiceImpl(MemberRepository memberRepository, JwtTokenHandler jwtTokenHandler) {
        this.memberRepository = memberRepository;
        this.jwtTokenHandler = jwtTokenHandler;
    }

    public Member userWithEncodedPassword(RegisterRequestDto registerRequestDto) {
        String encodedPassword = BCrypt.hashpw(registerRequestDto.password(), BCrypt.gensalt());
        return new Member(registerRequestDto.userRole(), registerRequestDto.email(), encodedPassword);
    }

    @Override
    public TokenResponseDto registerAndReturnToken(RegisterRequestDto registerRequestDto) {

        if (memberRepository.findUserByEmail(registerRequestDto.email()).isPresent()) {
            throw new EmailDuplicationException("중복된 이메일입니다");
        }

        Member member = userWithEncodedPassword(registerRequestDto);
        memberRepository.createUser(member);

        return new TokenResponseDto(jwtTokenHandler.createToken(member));
    }

    @Override
    public TokenResponseDto login(LoginRequestDto loginRequest) {
        Member storedMember = memberRepository.findUserByEmail(loginRequest.email())
            .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

        if (!BCrypt.checkpw(loginRequest.password(), storedMember.password())) {
            throw new InvalidPasswordException("비밀번호가 다릅니다");
        }

        return new TokenResponseDto(jwtTokenHandler.createToken(storedMember));
    }

    @Override
    public Optional<Member> getUserByEmail(String email) {
        return memberRepository.findUserByEmail(email);
    }
}
