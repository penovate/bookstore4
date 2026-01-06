package bookstore.controller;

import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import bookstore.dao.impl.ReviewsDAOImpl;

@Controller
public class DeleteReview {

    @Autowired
    private ReviewsDAOImpl reviewsDAO;

    @GetMapping("/DeleteReview")
    public String deleteReview(
            @RequestParam(value = "review_id", required = false) String reviewIdStr)
            throws Exception {

        String message;

        if (reviewIdStr == null || reviewIdStr.isEmpty()) {
            message = "刪除失敗：缺少評價ID！";
        } else {
            try {
                Integer reviewId = Integer.valueOf(reviewIdStr);
                int count = reviewsDAO.deleteReview(reviewId);

                if (count > 0) {
                    message = "評價資料刪除成功！";
                } else {
                    message = "刪除失敗：找不到該評價資料！";
                }
            } catch (NumberFormatException e) {
                message = "刪除失敗：評價ID格式錯誤！";
            }
        }

        return "redirect:/GetAllReviews?status=success&msg="
                + URLEncoder.encode(message, "UTF-8");
    }

    @PostMapping("/DeleteReview")
    public String deleteReviewPost(
            @RequestParam(value = "review_id", required = false) String reviewIdStr)
            throws Exception {

        // 行為與 GET 完全一致
        return deleteReview(reviewIdStr);
    }
}
