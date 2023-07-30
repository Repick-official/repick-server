package repick.repickserver.domain.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductState;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    List<Product> findTop4ByProductStateOrderByIdDesc(ProductState productState);
    Boolean existsByIdAndProductState(Long id, ProductState productState);
    Optional<Product> findByIdAndProductState(Long id, ProductState productState);
}
