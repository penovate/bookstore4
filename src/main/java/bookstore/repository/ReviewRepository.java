package bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bookstore.bean.ReviewBean;

public interface ReviewRepository extends JpaRepository<ReviewBean, Integer> {

}
