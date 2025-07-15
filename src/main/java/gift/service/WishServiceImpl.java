package gift.service;

import gift.dto.request.WishAddRequestDto;
import gift.dto.request.WishDeleteRequestDto;
import gift.dto.request.WishUpdateRequestDto;
import gift.dto.response.WishIdResponseDto;
import gift.entity.Wish;
import gift.exception.UnauthorizedWishListException;
import gift.exception.WishNotFoundException;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WishServiceImpl implements WishService {

    private final MemberRepository memberRepository;
    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishServiceImpl(MemberRepository memberRepository,
        WishRepository wishJdbcRepository, ProductRepository productRepository) {
        this.memberRepository = memberRepository;
        this.wishRepository = wishJdbcRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Long getProductIdByName(String productName) {
        return productRepository.findByName(productName).getId();
    }

    @Override
    public Long getMemberIdByEmail(String email) {
        return memberRepository.findMemberByEmail(email).orElseThrow().getId();
    }

    @Transactional
    @Override
    public WishIdResponseDto addProduct(WishAddRequestDto wishAddRequestDto, String email) {
        Wish wish = new Wish();
        wish.setMemberId(getMemberIdByEmail(email));
        wish.setProductId(
            getProductIdByName(wishAddRequestDto.productName()));

        wishRepository.save(wish);
        return new WishIdResponseDto(wish.getId());
    }

    @Transactional
    @Override
    public List<Wish> getWishList(String email) {
        return wishRepository.findAllById(
            List.of(getMemberIdByEmail(email)));
    }

    @Transactional
    @Override
    public void deleteProduct(
        String email,
        Long wishId,
        WishDeleteRequestDto wishDeleteRequestDto) {

        if (!memberRepository.findMemberByEmail(email).get().getId()
            .equals(wishId)) {
            throw new UnauthorizedWishListException("사용자의 위시 리스트가 아님");
        }
        if (wishRepository.findById(wishId).isEmpty()) {
            throw new WishNotFoundException("위시 항목이 존재하지 않음");
        }

        wishRepository.delete(wishRepository.findById(wishId).get());
    }

    @Transactional
    @Override
    public void updateProduct(
        Long wishId,
        String email,
        WishUpdateRequestDto wishUpdateRequestDto) {

        if (!memberRepository.findMemberByEmail(email).get().getId()
            .equals(wishId)) {
            throw new UnauthorizedWishListException("사용자의 위시 리스트가 아님");
        }

        Wish wish = wishRepository.findById(wishId).get();
        wish.setQuantity(wishUpdateRequestDto.quantity());

        wishRepository.save(wish);
    }
}
