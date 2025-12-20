package bookstore.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class Cart implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer cartId;
	private Integer userId;
	private Integer bookId;
	private Integer quantity;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private String cartStatus;

	public Cart() {
	}
	
	public Cart(Integer cartId, Integer userId, Integer bookId, Integer quantity, Timestamp createdAt,
			Timestamp updatedAt, String cartStatus) {
		super();
		this.cartId = cartId;
		this.userId = userId;
		this.bookId = bookId;
		this.quantity = quantity;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.cartStatus = cartStatus;
	}

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer cartId) {
		this.cartId = cartId;
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getCartStatus() {
		return cartStatus;
	}

	public void setCartStatus(String cartStatus) {
		this.cartStatus = cartStatus;
	}

	@Override
	public String toString() {
		return "Cart [cartId=" + cartId + ", userId=" + userId + ", bookId=" + bookId + ", quantity=" + quantity
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", cartStatus=" + cartStatus + "]";
	}
}
