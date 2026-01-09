package bookstore.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bookstore.bean.BooksBean;
import bookstore.bean.GenreBean;
import bookstore.exceptionCenter.BusinessException;
import bookstore.service.bookService;

@RestController
@RequestMapping("/books")
public class BookController {

	@Autowired
	private bookService bookService;

	@GetMapping("/booksIndex")
	public String bookIndex(Model model) {
		return "books/booksIndex";
	}

	@GetMapping("/getAllBooks")
	public String getAllBooks(Model model) {
		List<BooksBean> bookList = bookService.selectAllBooks();
		model.addAttribute("bookList", bookList);
		return "books/GetAllBooks";
	}

	@GetMapping("/getBook")
	public String selectBookById(@RequestParam("bookId") String idStr, Model model) {
		if (idStr == null || !idStr.matches("^\\d+$")) {
			throw new BusinessException(400, "格式錯誤:ID必須為數字");
		}
		BooksBean book = bookService.selectBookByIdS(Integer.parseInt(idStr));
		model.addAttribute("book", book);
		return "books/GetBook";
	}

	@GetMapping("/insertPage")
	public String InsertPage(Model model) {

		List<GenreBean> genreList = bookService.getAllGenres();
		model.addAttribute("genreList", genreList);

		return "books/InsertBook";
	}

	@PostMapping("/insert")
	public ResponseEntity<BooksBean> insertBook(@RequestPart("book") BooksBean book,@RequestPart(value = "file"
	,required = false)MultipartFile file) throws IOException {
		BooksBean newBook = bookService.insertBook(book, file);
		return ResponseEntity.ok(newBook);
	}

	@GetMapping("/updatePage")
	public String updatePage(@RequestParam("bookId") Integer bookId, Model model) {
		List<GenreBean> genreList = bookService.getAllGenres();
		model.addAttribute("genreList", genreList);

		BooksBean book = bookService.selectBookByIdS(bookId);
		model.addAttribute("book", book);
		return "books/UpdateBook";
	}

	@PutMapping("/update/{bookId}")
	public ResponseEntity<BooksBean> updateBook(@PathVariable Integer bookId, @RequestPart("book") BooksBean book,
			@RequestPart(value = "file", required = false) MultipartFile file)
			throws IllegalStateException, IOException {
		BooksBean updateBook = bookService.updateBook(bookId, book, file);
		return ResponseEntity.ok(updateBook);
	}

	@PostMapping("/updateStatus")
	public String updateOnShelf(@RequestParam("bookId") Integer bookId, @RequestParam("status") boolean status) {
		bookService.updateOnShelfStatus(bookId, status);
		return "redirect:/books/getAllBooks";
	}

	@DeleteMapping("/delete/{bookId}")
	public ResponseEntity<String> deleteBook(@PathVariable Integer bookId) {
		bookService.deleteBookById(bookId);
		return ResponseEntity.ok("書籍已成功刪除");
	}

	@ResponseBody
	@PostMapping("/isbnCheck")
	public String checkIsbn(@RequestParam("isbn") String isbn) {
		boolean exists = bookService.existsByIsbn(isbn);
		return String.valueOf(exists);
	}

	@ResponseBody
	@PostMapping("/updateOnShelfStatus")
	public String updateOnShelfStatus(@RequestParam("bookId") Integer bookId, @RequestParam("status") boolean status) {
		try {
			bookService.updateOnShelfStatus(bookId, status);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

	}

	@ResponseBody
	@PostMapping("/archiveBook")
	public String archiveBook(@RequestParam("bookId") Integer bookId) {
		bookService.archiveBook(bookId);

		return "success";
	}

	@ResponseBody
	@PostMapping("/unarchiveBook")
	public String unarchiveBook(@RequestParam("bookId") Integer bookId) {
		bookService.unarchiveBook(bookId);
		return "success";
	}
}
