package gift.service;

import gift.dto.request.ProductRequestDto;
import gift.dto.request.ProductUpdateRequestDto;
import gift.dto.response.ProductResponseDto;
import gift.entity.Product;
import gift.exception.KakaoApproveException;
import gift.exception.ProductNotFoundException;
import gift.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRespository) {
        this.productRepository = productRespository;
    }

    private static final String KAKAO_KEYWORD = "카카오";

    public ProductResponseDto productToResponseDto(Product product) {
        return new ProductResponseDto(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getImageURL());
    }


    public Product getProduct(long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다");
        }
        return productRepository.findById(productId).orElseThrow();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product findByName(String name) {
        return productRepository.findByName(name);
    }

    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        if (productRequestDto.name().contains(KAKAO_KEYWORD)) {
            throw new KakaoApproveException(
                "\"카카오\" 문구가 들어간 상품은 담당MD와 협의 후 사용할 수 있습니다");
        }
        Product product = new Product();
        product.setName(productRequestDto.name());
        product.setPrice(productRequestDto.price());
        product.setImageURL(productRequestDto.imageURL());
        productRepository.save(product);

        return productToResponseDto(product);
    }


    @Transactional
    public ProductResponseDto updateProduct(long productId,
        ProductUpdateRequestDto productUpdateRequestDto) {

        Product product = getProduct(productId);
        product.setName(productUpdateRequestDto.name());
        product.setPrice(productUpdateRequestDto.price());
        product.setImageURL(productUpdateRequestDto.imageURL());
        productRepository.save(product);

        return productToResponseDto(product);
    }


    @Transactional
    public void deleteProduct(long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다");
        }
        productRepository.delete(
            productRepository.findById(productId).orElseThrow());
    }


}
