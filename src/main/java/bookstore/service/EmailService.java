package bookstore.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	
	private final JavaMailSender mailSender;
	
	public void sendVerifyCode(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom("onlinebookstoreforjava@gmail.com");
		message.setTo(to);
		message.setSubject("網路書店系統 - 會員註冊驗證碼");
		message.setText("親愛的讀者您好：\n\n感謝您加入森林書屋！您的註冊驗證碼為：【 " + code + " 】\n\n該驗證碼於 5 分鐘內有效，請儘速完成註冊。");
		
		mailSender.send(message);
	}

}
