package gift.jpaTest;

import gift.entity.Member;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        Member member = new Member();
        member.setMemberRole("user");
        member.setEmail("test@gmail.com");
        member.setPassword("testpassword");

        memberRepository.save(member);
    }

    @AfterEach
    public void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    public void 유효하지_않은_이메일_회원가입시도_오류발생() {
        Member member = new Member();
        member.setMemberRole("user2");
        member.setEmail("testgmailcom");
        member.setPassword("testpassword");

        memberRepository.save(member);
    }
}
