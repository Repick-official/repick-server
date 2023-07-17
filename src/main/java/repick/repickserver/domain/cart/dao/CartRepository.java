package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.Cart;
import repick.repickserver.domain.member.domain.Member;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByMember(Member member);
}
