package gift.repository;

import gift.entity.Member;
import java.util.Optional;

public interface UserRepository {

    void createUser(Member member);

    Optional<Member> findUserByEmail(String email);

    Long findUserIdByEmail(String email);


}
