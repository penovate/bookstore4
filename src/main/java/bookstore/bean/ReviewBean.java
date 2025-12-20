package bookstore.bean;

public class ReviewBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String reviewId;
	private String userId;
	private String bookId;
    private String rating; 
    private String comment;
    private String createdAt;
    
	public String getReviewId() {
		return reviewId;
	}
	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	// join部分
	private String userName;
	private String bookName;

	public String getUserName() { return userName; }
	public void setUserName(String userName) { this.userName = userName; }

	public String getBookName() { return bookName; }
	public void setBookName(String bookName) { this.bookName = bookName; }
	
	@Override
	public String toString() {
		return "Bean [reviewId=" + reviewId + ", userId=" + userId + ", bookId=" + bookId + ", rating=" + rating
				+ ", comment=" + comment + ", createdAt=" + createdAt + "]";
	}
}
    
	
