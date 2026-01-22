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
        return reviewRepository.findAll();
    }

    // 查詢單筆（含 user、book）
    public ReviewBean findById(Integer reviewId) {
        return reviewRepository
                .findByIdWithUserAndBook(reviewId)
                .orElse(null);
    }

    // 新增 / 更新（JPA save 兩用）
    public ReviewBean save(ReviewBean review) {
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(LocalDateTime.now());
        }
        return reviewRepository.save(review);
    }

    // 刪除
    public void delete(Integer reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
