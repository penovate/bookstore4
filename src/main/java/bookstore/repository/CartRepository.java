package bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bookstore.bean.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    List<Cart> findByUserId(Integer userId);

    Optional<Cart> findByUserIdAndBookId(Integer userId, Integer bookId);

    void deleteByUserId(Integer userId);
}
