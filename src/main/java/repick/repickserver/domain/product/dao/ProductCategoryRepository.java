package repick.repickserver.domain.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.product.domain.ProductCategory;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findAllByProductId(Long productId);
}
