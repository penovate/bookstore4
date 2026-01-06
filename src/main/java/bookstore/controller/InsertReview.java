package bookstore.controller;

import java.time.LocalDateTime;

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
public class InsertReview {

    @Autowired
    private ReviewsDAOImpl reviewsDAO;

    // =============================
    // 顯示新增表單（GET）
    // =============================
    @GetMapping("/InsertReview")
    public String showInsertForm() {
        return "reviews/ReviewInsert";
    }

    // =============================
    // 處理新增（POST）
    // =============================
    @PostMapping("/InsertReview")
    public String insertReview(
            @RequestParam("user_id") String userIdStr,
            @RequestParam("book_id") String bookIdStr,
            @RequestParam("rating") String ratingStr,
            @RequestParam("comment") String comment,
            Model model) {

        String message;

        // --- 基本欄位檢查 ---
        if (userIdStr == null || userIdStr.isEmpty() ||
            bookIdStr == null || bookIdStr.isEmpty() ||
            ratingStr == null || ratingStr.isEmpty() ||
            comment == null || comment.isEmpty()) {

            message = "新增失敗！會員編號、書籍編號、評分與評論內容都不能為空白。";
            model.addAttribute("message", message);
            return "reviews/ReviewInsert";
        }

        Integer userId;
        Integer bookId;
        Integer rating;

        try {
            userId = Integer.valueOf(userIdStr);
            bookId = Integer.valueOf(bookIdStr);
            rating = Integer.valueOf(ratingStr);

            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            message = "新增失敗！評分必須是 1~5 的數字。";
            model.addAttribute("message", message);
            return "reviews/ReviewInsert";
        }

        // --- 建立 Bean ---
        ReviewBean review = new ReviewBean();
        BooksBean book = new BooksBean();
        book.setBookId(bookId);

        review.setBook(book);
        review.setBookId(bookId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        // --- 呼叫 DAO ---
        try {
            int result = reviewsDAO.insertReview(review);

            if (result > 0) {
                message = "新增書籍評論成功！";
                model.addAttribute("review", review);
                model.addAttribute("message", message);
                return "reviews/ReviewInsertFinish";
            } else {
                message = "新增失敗！請稍後再試。";
                model.addAttribute("message", message);
                return "reviews/ReviewInsert";
            }

        } catch (Exception e) {
            e.printStackTrace();
            message = "新增失敗！資料庫寫入錯誤，請檢查資料是否正確！";
            model.addAttribute("message", message);
            return "reviews/ReviewInsert";
        }
    }
}
