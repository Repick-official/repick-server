package repick.repickserver.domain.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductImage;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductInAndIsMainImage(List<Product> products, Boolean isMainImage);
}
