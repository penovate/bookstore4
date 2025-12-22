package bookstore.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReviewBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer reviewId;
	private Integer userId;
	private Integer bookId;
	private Integer rating;
	private String comment;
	private LocalDateTime createdAt;

	// join 顯示用
	private String userName;
	private String bookName;

	// ===== getter / setter =====
	public Integer getReviewId() {
		return reviewId;
	}

	public void setReviewId(Integer reviewId) {
		this.reviewId = reviewId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	@Override
	public String toString() {
		return "ReviewBean{" + "reviewId=" + reviewId + ", userId=" + userId + ", bookId=" + bookId + ", rating="
				+ rating + ", comment='" + comment + '\'' + ", createdAt=" + createdAt + '}';
	}
}
