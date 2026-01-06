package bookstore.dao.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bookstore.bean.ReviewBean;
import bookstore.dao.ReviewDAO;
import bookstore.repository.ReviewRepository;

@Service
public class ReviewsDAOImpl implements ReviewDAO {

	@Autowired
	private ReviewRepository reviewRepository;
    // =============================
    // 查詢所有評價
    // =============================
    @Override
    @Transactional
    public List<ReviewBean> selectAllReviews() {

    		List<ReviewBean> reviewReload =  reviewRepository.findAll();
     	
        return reviewReload;
    }

    // =============================
    // 依 reviewId 查詢單筆
    // =============================
	@Override
	@Transactional
    public ReviewBean selectReviewById(Integer reviewId) {

    	return reviewRepository
                .findByIdWithUserAndBook(reviewId)
                .orElse(null);
    }

    // =============================
    // 新增評價
    // =============================
    @Override
    @Transactional
    public int insertReview(ReviewBean review) {

    	// 若 createdAt 沒值，Java 端補（這一行保留是對的）
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(LocalDateTime.now());
        }

        reviewRepository.save(review);

        // 如果沒丟 Exception，就視為成功
        return 1;
    }

    // =============================
    // 更新評價
    // =============================
    @Override
    @Transactional
    public int updateReview(ReviewBean review) {

    		reviewRepository.save(review);

        // 沒丟 exception 就視為成功
        return 1;
    }

    // =============================
    // 刪除評價
    // =============================
	@Override
	@Transactional
    public int deleteReview(Integer reviewId) {

		  reviewRepository.deleteById(reviewId);

		    // 沒丟 Exception 就視為成功
		    return 1;
    }
}
