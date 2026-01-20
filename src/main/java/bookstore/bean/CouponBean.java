package bookstore.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupons")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })//避免在 JSON 轉換時，因為 Hibernate 的延遲加載（Lazy Loading）產生循環引用或代理物件報錯
public class CouponBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Integer couponId;

    @Column(name = "user_id")
    private Integer userId;

    // 優惠券代碼 ("read50"、"read100")
    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    // 折扣金額
    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    // 最低消費金額
    @Column(name = "min_spend", nullable = false)
    private BigDecimal minSpend;

    // 狀態: 0=未使用, 1=已使用
    @Column(name = "status", nullable = false)
    private Integer status;

    // 領取時間
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createdAt;

    // 使用時間
    @Column(name = "used_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp usedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties("coupons")
    private UserBean userBean;
}
