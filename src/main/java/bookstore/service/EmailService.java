package bookstore.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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

            InternetAddress fromAddress = new InternetAddress("onlinebookstoreforjava@gmail.com", "森林書屋 Forest Bookstore", "UTF-8");
            
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
                             "<p>如果您本人並未執行此操作，請立即聯繫我們的客服人員，或嘗試再次重設密碼以保護您的帳戶安全。</p>" +
                             "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                             "<small style='color: #888;'>此為系統自動發送，請勿直接回覆。</small>";
            
            helper.setText(content, true); 
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("安全性通知郵件發送失敗: " + e.getMessage());
        }
    }
}