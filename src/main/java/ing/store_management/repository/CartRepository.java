package ing.store_management.repository;

import ing.store_management.model.entity.Cart;
import ing.store_management.model.entity.Customer;
import ing.store_management.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);

}
