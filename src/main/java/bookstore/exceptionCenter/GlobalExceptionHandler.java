package bookstore.exceptionCenter;


import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	
	@ExceptionHandler(BusinessException.class)
	public String handleBusinessException(BusinessException ex, RedirectAttributes ra, HttpServletRequest request) {
		log.warn("業務異常[代碼:{}]:{}", ex.getCode(), ex.getMessage());
		ra.addFlashAttribute("status", "error");
		ra.addFlashAttribute("errorCode", ex.getCode());
		ra.addFlashAttribute("errorMsg", ex.getMessage());

		String referer = request.getHeader("Referer");

		return "redirect:" + (referer != null ? referer : "/login");
	}

}
