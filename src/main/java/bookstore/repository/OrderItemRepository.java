package bookstore.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bookstore.bean.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

}
