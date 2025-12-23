package bookstore.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Orders implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ORDER_ID")
	private Integer orderId;

//	建立UserBean物件，可以取代userId欄位
//	@Column(name = "USER_ID")
//	private Integer userId;

	@Column(name = "TOTAL_AMOUNT")
	private BigDecimal totalAmount;

	@Column(name = "PAYMENT_METHOD")
	private String paymentMethod;

	@Column(name = "PAYMENT_STATUS")
	private String paymentStatus;

	@Column(name = "ORDER_STATUS")
	private String orderStatus;

	@Column(name = "RECIPIENT_AT")
	private String recipientAt;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "PHONE")
	private String phone;

	@Column(name = "SHIPPED_AT")
	private Timestamp shippedAt;

	@Column(name = "DELIVERED_AT")
	private Timestamp deliveredAt;

	@Column(name = "RECEIVED_AT")
	private Timestamp receivedAt;

	@Column(name = "CREATED_AT")
	private Timestamp createdAt;

	@Column(name = "PAID_AT")
	private Timestamp paidAt;

	@Column(name = "COMPLETED_AT")
	private Timestamp completedAt;

	@Column(name = "UPDATED_AT")
	private Timestamp updatedAt;

	@OneToMany(mappedBy = "orders", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<OrderItem> items = new LinkedList<OrderItem>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserBean userBean = new UserBean();

	public Orders() {
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

//	public Integer getUserId() {
//		return userId;
//	}
//
//	public void setUserId(Integer userId) {
//		this.userId = userId;
//	}

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

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	@Override
	public String toString() {
		return "Orders [orderId=" + orderId + ", totalAmount=" + totalAmount + ", paymentMethod="
				+ paymentMethod + ", paymentStatus=" + paymentStatus + ", orderStatus=" + orderStatus + ", recipientAt="
				+ recipientAt + ", address=" + address + ", phone=" + phone + ", shippedAt=" + shippedAt
				+ ", deliveredAt=" + deliveredAt + ", receivedAt=" + receivedAt + ", createdAt=" + createdAt
				+ ", paidAt=" + paidAt + ", completedAt=" + completedAt + ", updatedAt=" + updatedAt + "]";
	}
}
