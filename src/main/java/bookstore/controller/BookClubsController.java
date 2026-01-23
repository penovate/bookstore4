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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import bookstore.aop.BusinessException;
import bookstore.bean.BookClubsBean;
import bookstore.service.BookClubService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/clubs")
@DynamicInsert
public class BookClubsController {

	@Autowired
	private BookClubService bookClubService;

	@GetMapping("/allClubs")
	public ResponseEntity<List<BookClubsBean>> getAllClubs() {
		List<BookClubsBean> list = bookClubService.getAllClubs();
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
													@RequestPart(value = "proofFile", required = false) MultipartFile proof,
													HttpServletRequest request) throws IOException {
		Integer userId = (Integer) request.getAttribute("userId");

		if (userId==null) {
			return ResponseEntity.status(401).body("請先登入");
		}
		
		BookClubsBean club = bookClubService.createBookClub(bookclub, proposal, proof, userId);
		return ResponseEntity.ok(club);
	}
	
	@DeleteMapping("/delete/{clubId}")
	public ResponseEntity<String> deleteClub(@PathVariable Integer clubId){
		bookClubService.deleteClubId(clubId);
		return ResponseEntity.noContent().build();
	}

}
