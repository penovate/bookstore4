package bookstore.controller;

import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import bookstore.bean.BooksBean;
import bookstore.bean.ReviewBean;
import bookstore.dao.impl.ReviewsDAOImpl;

@Controller
public class UpdateReview {

    @Autowired
    private ReviewsDAOImpl reviewsDAO;

    // =============================
    // 顯示更新頁面（GET）
    // =============================
    @GetMapping("/UpdateReview")
    public String showUpdateForm(
            @RequestParam(value = "review_id", required = false) String reviewIdStr,
            Model model) throws Exception {

        if (reviewIdStr == null || reviewIdStr.isEmpty()) {
            String msg = URLEncoder.encode("缺少評價ID", "UTF-8");
            return "redirect:/GetAllReviews?error=" + msg;
        }

        Integer reviewId = Integer.valueOf(reviewIdStr);
        ReviewBean review = reviewsDAO.selectReviewById(reviewId);

        if (review == null) {
            String msg = URLEncoder.encode("找不到該評價資料！", "UTF-8");
            return "redirect:/GetAllReviews?error=" + msg;
        }

        model.addAttribute("review", review);
        return "reviews/ReviewUpdate";
    }

    // =============================
    // 處理更新（POST）
    // =============================
    @PostMapping("/UpdateReview")
    public String updateReview(
            @RequestParam("review_id") String reviewIdStr,
            @RequestParam("user_id") String userIdStr,
            @RequestParam("book_id") String bookIdStr,
            @RequestParam("rating") String ratingStr,
            @RequestParam("comment") String comment,
            Model model) throws Exception {

        String message;

        if (comment == null || comment.trim().isEmpty()) {
            message = URLEncoder.encode("更新失敗！評論內容不能為空白！", "UTF-8");
            return "redirect:/UpdateReview?review_id=" + reviewIdStr + "&error=" + message;
        }

        Integer reviewId;
        Integer userId;
        Integer bookId;
        Integer rating;

        try {
            reviewId = Integer.valueOf(reviewIdStr);
            userId   = Integer.valueOf(userIdStr);
            bookId   = Integer.valueOf(bookIdStr);
            rating   = Integer.valueOf(ratingStr);

            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            message = URLEncoder.encode("更新失敗！評分必須是 1~5 的數字！", "UTF-8");
            return "redirect:/UpdateReview?review_id=" + reviewIdStr + "&error=" + message;
        }

        // === 組 Bean ===
        ReviewBean review = new ReviewBean();
        BooksBean book = new BooksBean();
        book.setBookId(bookId);

        review.setReviewId(reviewId);
        review.setUserId(userId);
        review.setBook(book);
        review.setRating(rating);
        review.setComment(comment);
        // createdAt 不動（更新不改建立時間）

        int result = reviewsDAO.updateReview(review);

        if (result > 0) {
            message = "評價更新成功！";
        } else {
            message = "更新失敗！找不到該評價或資料未變更！";
        }

        return "redirect:/GetAllReviews?status=success&msg="
                + URLEncoder.encode(message, "UTF-8");
    }
}
