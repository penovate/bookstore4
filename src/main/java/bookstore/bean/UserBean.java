package bookstore.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Component
@DynamicInsert
@DynamicUpdate
public class UserBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	@Column(name = "email")
	private String email;
	@Column(name = "user_pwd")
	private String userPwd;
	@Column(name = "user_name")
	private String userName;
	@Column(name = "gender")
	private String gender;
	@Column(name = "birth")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birth;
	@Column(name = "phone_num")
	private String phoneNum;
	@Column(name = "address")
	private String address;
	@Column(name = "img")
	private String img;
	@Column(name = "points")
	private Integer points;
	@Column(name = "user_type")
	private Integer userType;
	@Column(name = "status")
	private Integer status;
	@Column(name = "created_by")
	private String createdBy;
	@Column(name = "created_at", insertable = false, updatable = false)
	private Date createdAt;
	@Column(name = "updated_by")
	private String updatedBy;
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Date updatedAt;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<ReviewBean> reviews = new ArrayList<>();
//
	@OneToMany(mappedBy = "userBean", fetch = FetchType.LAZY)
	private List<Orders> orders = new ArrayList<>();

//	@OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
//	private List<Cart> carts = new ArrayList<>();

	public UserBean() {
	}

	public Integer getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public String getUserName() {
		return userName;
	}

	public String getGender() {
		return gender;
	}

	public Date getBirth() {
		return birth;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public String getAddress() {
		return address;
	}

	public String getImg() {
		return img;
	}

	public Integer getPoints() {
		return points;
	}

	public Integer getUserType() {
		return userType;
	}

	public Integer getStatus() {
		return status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public List<ReviewBean> getReviews() {
		return reviews;
	}

	public List<Orders> getOrders() {
		return orders;
	}
//
//	public List<Cart> getCarts() {
//		return carts;
//	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setReviews(List<ReviewBean> reviews) {
		this.reviews = reviews;
	}

	public void setOrders(List<Orders> orders) {
		this.orders = orders;
	}
//
//	public void setCarts(List<Cart> carts) {
//		this.carts = carts;
//	}

	@Override
	public String toString() {
		return "Bean [userId=" + userId + ", email=" + email + ", userPwd=" + userPwd + ", userName=" + userName
				+ ", gender=" + gender + ", birth=" + birth + ", phoneNum=" + phoneNum + ", address=" + address
				+ ", img=" + img + ", points=" + points + ", userType=" + userType + ", status=" + status
				+ ", createdBy=" + createdBy + ", createdAt=" + createdAt + ", updatedBy=" + updatedBy + ", updatedAt="
				+ updatedAt + "]";
	}
}
