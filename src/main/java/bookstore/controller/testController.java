package bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import bookstore.exceptionCenter.BusinessException;

@Controller
public class testController {

	@GetMapping("/test-error")
	public String testError() {
		throw new BusinessException(888, "這是一個手動處發的測試消息");
	}
}
