package bookstore.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonthlySalesDTO {
    private Integer year;
    private Integer month;
    private BigDecimal revenue;
    private Long orderCount;

    // Explicit constructor to handle potential Lombok issues or type ambiguities
    public MonthlySalesDTO(Integer year, Integer month, BigDecimal revenue, Long orderCount) {
        this.year = year;
        this.month = month;
        this.revenue = revenue != null ? revenue : BigDecimal.ZERO;
        this.orderCount = orderCount != null ? orderCount : 0L;
    }

    // 使用AllArgsConstructor發現無法正確轉型，所以寫建構子並進行手動轉型
    public MonthlySalesDTO(Object year, Object month, Object revenue, Object orderCount) {
        this.year = (year instanceof Integer y) ? y.intValue() : 0;
        this.month = (month instanceof Integer m) ? m.intValue() : 0;

        if (revenue instanceof BigDecimal r1) {
            this.revenue = r1;
        } else if (revenue instanceof Integer r2) {
            this.revenue = BigDecimal.valueOf(r2.doubleValue());
        } else {
            this.revenue = BigDecimal.ZERO;
        }

        // Count()回傳類型固定為Long
        this.orderCount = (orderCount instanceof Long o) ? o.longValue() : 0L;
    }
}
