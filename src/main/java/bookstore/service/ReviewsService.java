package bookstore.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bookstore.bean.ReviewBean;
import bookstore.repository.ReviewRepository;

@Service
@Transactional
public class ReviewsService {
	@Autowired
    private ReviewRepository reviewRepository;

    // 查詢全部
    public List<ReviewBean> findAllReviews() {
    	
    	List<ReviewBean> reviews = reviewRepository.findAll();
    	
    	for (ReviewBean review : reviews) {
            if (review.getUser() != null) {
                review.setUserType(review.getUser().getUserType());
            }
        }
    	
        return reviews;
    }

    // 查詢單筆（含 user、book）
    public ReviewBean findById(Integer reviewId) {

    	ReviewBean review = reviewRepository
                .findByIdWithUserAndBook(reviewId)
                .orElse(null);
        
        if (review != null && review.getUser() != null) {
            review.setUserType(review.getUser().getUserType());
        }
        
        return review;
    }

    // 新增 / 更新（JPA save 兩用）
    public ReviewBean save(ReviewBean review) {
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(LocalDateTime.now());
        }
        
        ReviewBean savedReview = reviewRepository.save(review);
        
        if (savedReview.getUser() != null) {
            savedReview.setUserType(savedReview.getUser().getUserType());
        }
        
        return savedReview;
    }

    // 刪除
    public void delete(Integer reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
