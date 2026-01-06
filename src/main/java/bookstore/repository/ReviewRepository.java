package bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import bookstore.bean.ReviewBean;

public interface ReviewRepository extends JpaRepository<ReviewBean, Integer> {
	 @Query("""
	           SELECT r
	           FROM ReviewBean r
	           JOIN FETCH r.user
	           JOIN FETCH r.book
	           WHERE r.reviewId = :id
	           """)
	    Optional<ReviewBean> findByIdWithUserAndBook(@Param("id") Integer id);
}
