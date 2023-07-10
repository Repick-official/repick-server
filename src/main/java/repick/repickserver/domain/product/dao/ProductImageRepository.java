package repick.repickserver.domain.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.product.domain.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
