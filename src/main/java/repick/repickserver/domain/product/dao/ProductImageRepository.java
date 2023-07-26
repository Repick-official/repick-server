package repick.repickserver.domain.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductImage;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductInAndIsMainImage(List<Product> products, Boolean isMainImage);
    Optional<ProductImage> findByProductAndIsMainImage(Product product, Boolean isMainImage);
    List<ProductImage> findAllByProductId(Long productId);
}
