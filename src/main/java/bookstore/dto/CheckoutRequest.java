package bookstore.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String recipientName;
    private String recipientPhone;
    private String deliveryMethod; // "HOME" or "STORE"
    private String address; // Address or Store Name
    private String paymentMethod;
}
