package repick.repickserver.domain.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.product.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
