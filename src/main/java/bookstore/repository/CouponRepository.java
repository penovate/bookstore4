package bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bookstore.bean.CouponBean;

@Repository
public interface CouponRepository extends JpaRepository<CouponBean, Integer> {
    List<CouponBean> findByUserId(Integer userId);

    // Check if user already claimed this specific code logic (optional constraint)
    // For this implementation, maybe we allow multiples or check manually in
    // service.
    // Let's allow fetching by userId and code to check duplicates.
    List<CouponBean> findByUserIdAndCouponCode(Integer userId, String couponCode);
}
