package bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bookstore.bean.CouponBean;

@Repository
public interface CouponRepository extends JpaRepository<CouponBean, Integer> {
	
	// 根據用戶 ID 查詢其擁有的所有優惠券
	List<CouponBean> findByUserId(Integer userId);

    // 透過用戶 ID 與優惠券代碼進行查詢，以判斷是否存在重複領取的情況。
    List<CouponBean> findByUserIdAndCouponCode(Integer userId, String couponCode);
}
