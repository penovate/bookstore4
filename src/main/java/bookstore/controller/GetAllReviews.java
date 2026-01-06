package bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import bookstore.bean.ReviewBean;
import bookstore.dao.impl.ReviewsDAOImpl;

@Controller
public class GetAllReviews {

    @Autowired
    private ReviewsDAOImpl reviewsDAO;

    @GetMapping("/GetAllReviews")
    public String getAllReviews(Model model) {

        List<ReviewBean> reviews = reviewsDAO.selectAllReviews();
        model.addAttribute("reviews", reviews);

        return "reviews/ReviewList";
    }

    @PostMapping("/GetAllReviews")
    public String postGetAllReviews(Model model) {
        return getAllReviews(model);
    }
}

