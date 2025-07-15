package gift.repository;


import gift.entity.Member;
import gift.exception.UserNotFoundException;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbcRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public UserJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(this.jdbcTemplate)
            .withTableName("users")
            .usingColumns("user_role", "email", "password");
    }

    private RowMapper<Member> userRowMapper() {
        return (rs, rowNum) -> new Member(
            rs.getString("user_role"),
            rs.getString("email"),
            rs.getString("password")
        );
    }

    @Override
    public void createUser(Member member) {

        Map<String, Object> parameters = Map.of(
            "user_role", member.userRole(),
            "email", member.email(),
            "password", member.password());

        jdbcInsert.execute(parameters);
    }

    @Override
    public Optional<Member> findUserByEmail(String email) {
        try {
            Member member = jdbcTemplate.queryForObject("select * from users where email = ?",
                userRowMapper(),
                email);
            return Optional.ofNullable(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Long findUserIdByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                "select user_id from users where email = ?",
                Long.class,
                email
            );
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다");
        }

    }
}
