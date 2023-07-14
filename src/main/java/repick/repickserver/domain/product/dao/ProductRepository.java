package repick.repickserver.domain.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductState;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop4ByProductStateOrderByIdDesc(ProductState productState);
}
