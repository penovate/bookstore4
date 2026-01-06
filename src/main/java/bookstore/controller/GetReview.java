package bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import bookstore.bean.ReviewBean;
import bookstore.dao.impl.ReviewsDAOImpl;

@Controller
public class GetReview {

    @Autowired
    private ReviewsDAOImpl reviewsDAO;

    @GetMapping("/GetReview")
    public String getReview(
            @RequestParam("reviewId") Integer reviewId,
            Model model) {

        ReviewBean review = reviewsDAO.selectReviewById(reviewId);
        model.addAttribute("review", review);

        return "reviews/GetReview";
    }

    @PostMapping("/GetReview")
    public String postGetReview(
            @RequestParam("reviewId") Integer reviewId,
            Model model) {

        return getReview(reviewId, model);
    }
}
