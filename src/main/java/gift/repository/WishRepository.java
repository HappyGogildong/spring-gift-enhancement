package gift.repository;

import gift.entity.Wish;
import java.util.List;

public interface WishRepository {

    long addProduct(String productName, String email);

    List<Wish> getWishList(String email);

    int deleteProduct(Long wishId, String productName);

    int updateWish(Long wishId, String productName, int quantity);

}
