package gift.service;

import gift.dto.request.WishAddRequestDto;
import gift.dto.request.WishDeleteRequestDto;
import gift.dto.request.WishUpdateRequestDto;
import gift.dto.response.WishIdResponseDto;
import gift.dto.response.WishResponseDto;
import gift.entity.Wish;
import gift.exception.ProductNotFoundException;
import gift.exception.UnauthorizedWishListException;
import gift.exception.WishNotFoundException;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
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
    public Long getMemberIdByEmail(String email) {
        return memberRepository.findMemberByEmail(email).getId();
    }

    @Transactional
    @Override
    public WishIdResponseDto addProduct(WishAddRequestDto wishAddRequestDto, String email) {
        Wish wish = new Wish();
        wish.setMember(memberRepository.findMemberByEmail(email));
        wish.setProduct(productRepository.findByName(wishAddRequestDto.productName()));
        wish.setQuantity(1);

        if (wish.getProduct() == null) {
            throw new ProductNotFoundException(wishAddRequestDto.productName());
        }

        wishRepository.save(wish);
        return new WishIdResponseDto(wish.getId());
    }

    @Transactional
    public List<WishResponseDto> getWishList(String email) {
        Long memberId = getMemberIdByEmail(email);
        List<Wish> wishes = wishRepository.findByMemberId(memberId);
        return wishes.stream()
            .map(WishResponseDto::new)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteProduct(
        String email,
        Long wishId,
        WishDeleteRequestDto wishDeleteRequestDto) {

        if (!memberRepository.findMemberByEmail(email).getId()
            .equals(wishId)) {
            throw new UnauthorizedWishListException("사용자의 위시 리스트가 아님");
        }
        Wish wish = wishRepository.findById(wishId)
            .orElseThrow(() -> new WishNotFoundException("위시 항목이 존재하지 않음"));

        wishRepository.delete(wish);
    }

    @Transactional
    @Override
    public void updateProduct(
        Long wishId,
        String email,
        WishUpdateRequestDto wishUpdateRequestDto) {

        if (!memberRepository.findMemberByEmail(email).getId()
            .equals(wishId)) {
            throw new UnauthorizedWishListException("사용자의 위시 리스트가 아님");
        }

        Wish wish = wishRepository.findById(wishId)
            .orElseThrow(() -> new WishNotFoundException("위시 항목이 존재하지 않음"));
        wish.setQuantity(wishUpdateRequestDto.quantity());

        wishRepository.save(wish);
    }
}
