package bookstore.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import bookstore.bean.BooksBean;
import bookstore.bean.ReviewBean;
import bookstore.service.ReviewsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ReviewController {

	@Autowired
	private ReviewsService reviewsService;

	// =================================================
	// 【原 GetAllReviews.java】
	// =================================================
	@GetMapping("/GetAllReviews")
	public String getAllReviews(Model model) {

		List<ReviewBean> reviews = reviewsService.findAllReviews();
		model.addAttribute("reviews", reviews);

		return "reviews/ReviewList";
	}

	@PostMapping("/GetAllReviews")
	public String postGetAllReviews(Model model) {
		return getAllReviews(model);
	}

	// =================================================
	// 【原 GetReview.java】
	// =================================================
	@GetMapping("/GetReview")
	public String getReview(HttpServletRequest request, Model model) {

		String reviewIdStr = request.getParameter("reviewId");
		Integer reviewId = Integer.valueOf(reviewIdStr);

		ReviewBean review = reviewsService.findById(reviewId);
		model.addAttribute("review", review);

		return "reviews/GetReview";
	}

	// =================================================
	// 【原 InsertReview.java - doGet】
	// =================================================
	@GetMapping("/InsertReview")
	public String showInsertPage() {
		return "reviews/ReviewInsert";
	}

	// =================================================
	// 【原 InsertReview.java - doPost】
	// =================================================
	@PostMapping("/InsertReview")
	public String insertReview(HttpServletRequest request, Model model) throws IOException {

		request.setCharacterEncoding("UTF-8");
		String message = "";

		String userIdStr = request.getParameter("user_id");
		String bookIdStr = request.getParameter("book_id");
		String ratingStr = request.getParameter("rating");
		String comment = request.getParameter("comment");

		if (userIdStr == null || userIdStr.isEmpty() || bookIdStr == null || bookIdStr.isEmpty() || ratingStr == null
				|| ratingStr.isEmpty() || comment == null || comment.isEmpty()) {

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

		ReviewBean review = new ReviewBean();
		BooksBean book = new BooksBean();
		book.setBookId(bookId);

		review.setBook(book);
		review.setBookId(bookId);
		review.setUserId(userId);
		review.setRating(rating);
		review.setComment(comment);
		review.setCreatedAt(LocalDateTime.now());

		reviewsService.save(review);

		message = "新增書籍評論成功！";
		model.addAttribute("review", review);
		model.addAttribute("message", message);
		return "reviews/ReviewInsertFinish";
	}

	// =================================================
	// 【原 UpdateReview.java - doGet】
	// =================================================
	@GetMapping("/UpdateReview")
	public String showUpdatePage(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {

		request.setCharacterEncoding("UTF-8");
		String reviewIdStr = request.getParameter("review_id");

		if (reviewIdStr == null || reviewIdStr.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/GetAllReviews?error=缺少評價ID");
			return null;
		}

		Integer reviewId = Integer.valueOf(reviewIdStr);
		ReviewBean review = reviewsService.findById(reviewId);

		if (review != null) {
			model.addAttribute("review", review);
			return "reviews/ReviewUpdate";
		} else {
			response.sendRedirect(request.getContextPath() + "/GetAllReviews?error=找不到該評價資料！");
			return null;
		}
	}

	// =================================================
	// 【原 UpdateReview.java - doPost】
	// =================================================
	@PostMapping("/UpdateReview")
	public void updateReview(HttpServletRequest request, HttpServletResponse response) throws IOException {

		request.setCharacterEncoding("UTF-8");
		String message = "";

		String reviewIdStr = request.getParameter("review_id");
		String userIdStr = request.getParameter("user_id");
		String bookIdStr = request.getParameter("book_id");
		String ratingStr = request.getParameter("rating");
		String comment = request.getParameter("comment");

		Integer reviewId = Integer.valueOf(reviewIdStr);
		Integer userId = Integer.valueOf(userIdStr);
		Integer bookId = Integer.valueOf(bookIdStr);
		Integer rating = Integer.valueOf(ratingStr);

		ReviewBean review = new ReviewBean();
		BooksBean book = new BooksBean();
		book.setBookId(bookId);

		review.setReviewId(reviewId);
		review.setUserId(userId);
		review.setBook(book);
		review.setRating(rating);
		review.setComment(comment);

		reviewsService.save(review);

		message = "評價更新成功！";
		response.sendRedirect(request.getContextPath() + "/GetAllReviews?msg=" + URLEncoder.encode(message, "UTF-8"));
	}

	// =================================================
	// 【原 DeleteReview.java】
	// =================================================
	@GetMapping("/DeleteReview")
	public void deleteReview(HttpServletRequest request, HttpServletResponse response) throws IOException {

		request.setCharacterEncoding("UTF-8");
		String message = "";

		String reviewIdStr = request.getParameter("review_id");

		if (reviewIdStr == null || reviewIdStr.isEmpty()) {
			message = "刪除失敗：缺少評價ID！";
		} else {
			try {
				Integer reviewId = Integer.valueOf(reviewIdStr);
				reviewsService.delete(reviewId);
				message = "評價資料刪除成功！";

			} catch (NumberFormatException e) {
				message = "刪除失敗：評價ID格式錯誤！";
			}
		}

		response.sendRedirect(
				request.getContextPath() + "/GetAllReviews?status=success&msg=" + URLEncoder.encode(message, "UTF-8"));
	}

	// 【Vue - 取得所有評論（JSON）】
	@GetMapping("/api/public/admin/reviews")
	@ResponseBody
	public List<ReviewBean> getAllReviewsForVue() {
		return reviewsService.findAllReviews();
	}

	// 【Vue - 取得單筆評論（JSON）】
	@GetMapping("/api/public/admin/reviews/{id}")
	@ResponseBody
	public ReviewBean getReviewForVue(@PathVariable Integer id) {
		return reviewsService.findById(id);
	}

	// 【Vue - 新增評論（JSON）】
	@PostMapping("/api/public/admin/reviews")
	@ResponseBody
	public ReviewBean insertReviewForVue(@RequestBody ReviewBean review) {

		review.setCreatedAt(LocalDateTime.now());
		reviewsService.save(review);

		return review;
	}

	// 【Vue - 修改評論（JSON）】
	@PutMapping("/api/public/admin/reviews/{id}")
	@ResponseBody
	public ReviewBean updateReviewForVue(@PathVariable Integer id, @RequestBody ReviewBean review) {

		ReviewBean dbReview = reviewsService.findById(id);

		if (dbReview == null) {
			throw new RuntimeException("找不到評價");
		}

		dbReview.setRating(review.getRating());
		dbReview.setComment(review.getComment());

		reviewsService.save(dbReview);

		return dbReview;
	}
	
	// 【Vue - 刪除評論（JSON）】
	@DeleteMapping("/api/public/admin/reviews/{id}")
	@ResponseBody
	public void deleteReviewForVue(@PathVariable Integer id) {
	    reviewsService.delete(id);
	}

}
