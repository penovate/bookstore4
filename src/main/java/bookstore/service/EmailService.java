package bookstore.service;

import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import bookstore.bean.OrderItem;
import bookstore.bean.Orders;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerifyCode(String to, String code, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            InternetAddress fromAddress = new InternetAddress("onlinebookstoreforjava@gmail.com",
                    "森林書屋 Forest Bookstore", "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            String actionText = subject.contains("重設") ? "重設您的帳戶密碼" : "註冊森林書屋會員";

            String content = "<h3>親愛的讀者您好：</h3>" +
                    "<p>您正在進行 " + actionText + "，您的驗證碼為：</p>" +
                    "<h1 style='color: #2E5C43; letter-spacing: 5px;'>" + code + "</h1>" +
                    "<p>請於 5 分鐘內完成驗證。為了您的帳戶安全，請勿將此代碼告知他人。</p>" +
                    "<p>若非本人操作，請忽略此信。</p>" +
                    "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                    "<small style='color: #888;'>此為系統自動發送，請勿直接回覆。</small>";

            helper.setText(content, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("郵件發送失敗原因: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("發送郵件失敗: " + e.getMessage());
        }
    } 
    
    public void sendResetSuccessNotification(String to) {
    	try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            InternetAddress fromAddress = new InternetAddress("onlinebookstoreforjava@gmail.com", "森林書屋 Forest Bookstore", "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject("森林書屋 - 帳戶密碼變更通知");

            String content = "<h3>親愛的讀者您好：</h3>" +
                             "<p>您的森林書屋帳戶密碼已於剛才 **成功完成重設**。</p>" +
                             "<p>如果您本人並未執行此操作，請立即聯繫我們的客服人員。</p>" +
                             "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                             "<small style='color: #888;'>此為系統自動發送，請勿直接回覆。</small>";

            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("安全性通知郵件發送失敗: " + e.getMessage());
        }
    }

    public void sendOrderNotification(String to, Orders order, List<OrderItem> items) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            InternetAddress fromAddress = new InternetAddress("onlinebookstoreforjava@gmail.com",
                    "森林書屋 Forest Bookstore",
                    "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject("森林書屋 - 訂單確認通知 (訂單編號: " + order.getOrderId() + ")");

            StringBuilder itemsHtml = new StringBuilder();
            itemsHtml.append("<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>");
            itemsHtml.append(
                    "<tr style='background-color: #f2f2f2;'><th style='padding: 8px; text-align: left;'>商品名稱</th><th style='padding: 8px; text-align: right;'>數量</th><th style='padding: 8px; text-align: right;'>小計</th></tr>");

            // 使用傳入的 items 清單
            if (items != null) {
                for (bookstore.bean.OrderItem item : items) {
                    String bookName = "未知書籍";
                    if (item.getBooksBean() != null) {
                        bookName = item.getBooksBean().getBookName();
                    }
                    itemsHtml.append("<tr>");
                    itemsHtml.append("<td style='padding: 8px; border-bottom: 1px solid #ddd;'>").append(bookName)
                            .append("</td>");
                    itemsHtml.append("<td style='padding: 8px; text-align: right; border-bottom: 1px solid #ddd;'>")
                            .append(item.getQuantity()).append("</td>");
                    itemsHtml.append("<td style='padding: 8px; text-align: right; border-bottom: 1px solid #ddd;'>$")
                            .append(item.getPrice()).append("</td>");
                    itemsHtml.append("</tr>");
                }
            }
            itemsHtml.append("</table>");

            String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;'>"
                    +
                    "<h2 style='color: #2E5C43; text-align: center;'>感謝您的訂購！</h2>" +
                    "<p>親愛的讀者您好，我們已收到您的訂單。</p>" +
                    "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                    "<h4 style='color: #333;'></h4>" +
                    "<p><strong>訂單編號：</strong> " + order.getOrderId() + "</p>" +
                    "<p><strong>訂單日期：</strong> " + order.getCreatedAt() + "</p>" +
                    "<p><strong>付款方式：</strong> " + order.getPaymentMethod() + "</p>" +
                    "<p><strong>配送方式：</strong> " + order.getDeliveryMethod() + "</p>" +
                    "<p><strong>收件地址：</strong> " + order.getAddress() + "</p>" +
                    "<h4 style='color: #333;'>訂單明細</h4>" +
                    itemsHtml.toString() +
                    "<p style='text-align: right; margin-top: 10px;'><strong>運費：</strong> $" + order.getShippingFee()
                    + "</p>" +
                    "<h3 style='text-align: right; color: #d9534f;'>總金額：$" + order.getFinalAmount() + "</h3>" +
                    "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                    "<p style='font-size: 14px; color: #666;'>您可以登入會員中心查看訂單詳細狀態。</p>" +
                    "<div style='text-align: center; margin-top: 30px;'>" +
                    "<a href='http://localhost:5173' style='background-color: #2E5C43; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>前往森林書屋</a>"
                    +
                    "</div>" +
                    "</div>";

            helper.setText(content, true);

            mailSender.send(mimeMessage);
            System.out.println("訂單通知信已發送至: " + to);
        } catch (Exception e) {
            System.err.println("訂單通知信發送失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }
}