package bookstore.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.ToString;

@Entity
@Table(name = "reviews")
@Component
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user", "book"})
public class ReviewBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Integer reviewId; 

	// ===== FK → ManyToOne（關鍵）=====

	// ✅ ManyToOne：同一個欄位做關聯，但設為「只讀」，避免重複 mapping
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@JsonIgnore
	@ToString.Exclude
	private UserBean user;
//
	@ManyToOne(fetch = FetchType.LAZY )
	@JoinColumn(name = "book_id", nullable = false)
	@JsonIgnore
	private BooksBean book;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@Column(name = "book_id", insertable = false, updatable = false)
	private Integer bookId;

	// ===== 其他欄位 =====

	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Column(name = "comment")
	private String comment;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	// ====== 過渡期：舊 JSP 可能用到 ======
	@Transient
	private String userName;

	@Transient
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
		if (this.book == null) {
			this.book = new BooksBean();
		}
		this.book.setBookId(bookId);
	}

	public UserBean getUser() {
		return user;
	}

	// 這個現在可用可不用
	public void setUser(UserBean user) {
		this.user = user;
	}

	public BooksBean getBook() {
		return book;
	}

	// 這個現在可用可不用
	public void setBook(BooksBean book) {
		this.book = book;
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

	// ✅ 舊 JSP 仍可用：優先從關聯拿名稱（不會再一直 null）
	public String getUserName() {
		return (user != null) ? user.getUserName() : userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBookName() {
		return (book != null) ? book.getBookName() : bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	@Override
	public String toString() {
		return "ReviewBean [reviewId=" + reviewId + ", user=" + user + ", book=" + book + ", userId=" + userId
				+ ", rating=" + rating + ", comment=" + comment + ", createdAt=" + createdAt + ", userName=" + userName
				+ ", bookName=" + bookName + "]";
	}

}
