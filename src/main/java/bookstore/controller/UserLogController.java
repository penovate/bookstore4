package bookstore.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bookstore.bean.UserLogBean;
import bookstore.repository.UserLogRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class UserLogController {

	private final UserLogRepository logRepo;
	
	@GetMapping("/list")
	public List<UserLogBean> getLogs() {
		return logRepo.findAll(Sort.by(Sort.Direction.DESC, "actionTime"));
	}
}
