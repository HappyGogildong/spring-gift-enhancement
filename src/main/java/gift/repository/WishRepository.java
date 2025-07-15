package gift.repository;

import gift.entity.Wish;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

}
