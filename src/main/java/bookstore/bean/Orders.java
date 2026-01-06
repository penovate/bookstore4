package bookstore.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
	private Timestamp createdAt = new Timestamp(System.currentTimeMillis());;

	@Column(name = "PAID_AT")
	private Timestamp paidAt;

	@Column(name = "COMPLETED_AT")
	private Timestamp completedAt;

	@Column(name = "UPDATED_AT")
	private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());;

	@OneToMany(mappedBy = "orders", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private List<OrderItem> items = new LinkedList<OrderItem>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private UserBean userBean;
}
