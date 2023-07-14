package repick.repickserver.domain.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.product.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
