package bookstore.controller;

import java.io.IOException;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import bookstore.aop.BusinessException;
import bookstore.bean.BookClubsBean;
import bookstore.bean.ClubCategoriesBean;
import bookstore.bean.ClubConstants;
import bookstore.service.BookClubService;
import bookstore.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/clubs")
@DynamicInsert
public class BookClubsController {

	@Autowired
	private BookClubService bookClubService;
	@Autowired
	private JwtUtil jwtUtil;

	@GetMapping("/allClubs")
	public ResponseEntity<List<BookClubsBean>> getAllClubs() {
		List<BookClubsBean> list = bookClubService.getAllClubs();
		return ResponseEntity.ok(list);
	}

	@GetMapping("/categories")
	public ResponseEntity<List<ClubCategoriesBean>> getAllCategories() {
		List<ClubCategoriesBean> list = bookClubService.getAllCategories();
		return ResponseEntity.ok(list);
	}

	@GetMapping("/clubs/{clubId}")
	public ResponseEntity<BookClubsBean> getClub(@PathVariable("clubId") Integer clubId) {
		if (clubId == null) {
			throw new BusinessException(400, "ID不可空白");
		}
		BookClubsBean club = bookClubService.getClub(clubId);
		if (club == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(club);

	}

	@PostMapping("/insert")
	public ResponseEntity<?> createClub(@RequestPart("bookclub") BookClubsBean bookclub,
			@RequestPart("proposalFile") MultipartFile proposal,
			@RequestPart(value = "proofFile", required = false) MultipartFile proof, HttpServletRequest request)
			throws IOException {
		Integer userId = (Integer) request.getAttribute("userId");

		if (userId == null) {
			return ResponseEntity.status(401).body("請先登入");
		}

		BookClubsBean club = bookClubService.createBookClub(bookclub, proposal, proof, userId);
		return ResponseEntity.ok(club);
	}

	@GetMapping("/my-hosted")
	public ResponseEntity<List<BookClubsBean>> getMyHostedClubs(HttpServletRequest request) {
		Integer userId = (Integer) request.getAttribute("userId");
		if (userId == null) {
			return ResponseEntity.status(401).build();
		}
		// 這裡需要透過 Service 呼叫 UserRepository 查 UserBean，或是 Service 增加接受 userId 的方法
		// 由於 Service 有 findByHost(UserBean)，我們先在 Controller 轉換
		// 但 Controller 不直接碰 Repo，所以我們在 Service 加一個 findByHostId 比較好，
		// 或者直接用既有的 service.findByHost(userRepository.findById(userId))
		// 為了乾淨，我们在 Service 加一个 overload 或是直接修改 Controller 注入 UserRepo (不建議)。
		// 最佳解：Service 新增 findByHostId(Integer userId)

		// 暫時解：先在 Service 加方法，稍後透過工具修改 Service
		// 假設 Service 已更新。我們等下會去改 Service。
		List<BookClubsBean> list = bookClubService.findByHostId(userId);
		return ResponseEntity.ok(list);
	}

	@DeleteMapping("/delete/{clubId}")
	public ResponseEntity<String> deleteClub(@PathVariable Integer clubId) {
		bookClubService.deleteClubId(clubId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/update/{clubId}")
	public ResponseEntity<?> updateClub(@PathVariable Integer clubId, @RequestPart("data") BookClubsBean clubdata,
			@RequestPart(value = "proposal", required = false) MultipartFile proposalFile,
			@RequestPart(value = "proof", required = false) MultipartFile proofFile, HttpServletRequest request,
			@RequestHeader(value = "Authorization", required = false) String authHeader)
			throws IllegalStateException, IOException {

		Integer userId = (Integer) request.getAttribute("userId");

		if (userId == null) {
			return ResponseEntity.status(401).body("無法辨識使用者身分，請重新登入");
		}

		Integer userRole = getRoleFromToken(authHeader);

		if (userRole == null) {
			userRole = ClubConstants.ROLE_MEMBER;
		}

		BookClubsBean club = bookClubService.updateBookclub(clubId, clubdata, proposalFile, proofFile, userId,
				userRole);

		return ResponseEntity.ok(club);

	}

	// 從JWT中解析出Token
	private Integer getRoleFromToken(String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				// 使用您的 JwtUtil 讀取角色字串
				String roleStr = jwtUtil.getRole(token);

				// 將字串權限轉換為 Service 看得懂的整數常數
				// 假設 Token 裡的字串是 "SUPER_ADMIN", "ADMIN", "MEMBER"
				if ("SUPER_ADMIN".equals(roleStr)) {
					return ClubConstants.ROLE_SUPER_ADMIN; // 0
				} else if ("ADMIN".equals(roleStr)) {
					return ClubConstants.ROLE_ADMIN; // 1
				} else {
					return ClubConstants.ROLE_MEMBER; // 2 (預設為會員)
				}
			} catch (Exception e) {
				// Token 解析失敗，回傳 null
				return null;
			}
		}
		return null;
	}

}
