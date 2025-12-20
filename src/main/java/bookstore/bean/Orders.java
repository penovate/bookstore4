package bookstore.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Orders implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer orderId;
	private Integer userId;
	private BigDecimal totalAmount;
	private String paymentMethod;
	private String paymentStatus;
	private String orderStatus;
	private String recipientAt;
	private String address;
	private String phone;
	private Timestamp shippedAt;
	private Timestamp deliveredAt;
	private Timestamp receivedAt;
	private Timestamp createdAt;
	private Timestamp paidAt;
	private Timestamp completedAt;
	private Timestamp updatedAt;

	public Orders() {
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getRecipientAt() {
		return recipientAt;
	}

	public void setRecipientAt(String recipientAt) {
		this.recipientAt = recipientAt;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Timestamp getShippedAt() {
		return shippedAt;
	}

	public void setShippedAt(Timestamp shippedAt) {
		this.shippedAt = shippedAt;
	}

	public Timestamp getDeliveredAt() {
		return deliveredAt;
	}

	public void setDeliveredAt(Timestamp deliveredAt) {
		this.deliveredAt = deliveredAt;
	}

	public Timestamp getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(Timestamp receivedAt) {
		this.receivedAt = receivedAt;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(Timestamp paidAt) {
		this.paidAt = paidAt;
	}

	public Timestamp getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Timestamp completedAt) {
		this.completedAt = completedAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "Orders [orderId=" + orderId + ", userId=" + userId + ", totalAmount=" + totalAmount + ", paymentMethod="
				+ paymentMethod + ", paymentStatus=" + paymentStatus + ", orderStatus=" + orderStatus + ", recipientAt="
				+ recipientAt + ", address=" + address + ", phone=" + phone + ", shippedAt=" + shippedAt
				+ ", deliveredAt=" + deliveredAt + ", receivedAt=" + receivedAt + ", createdAt=" + createdAt
				+ ", paidAt=" + paidAt + ", completedAt=" + completedAt + ", updatedAt=" + updatedAt + "]";
	}
}
