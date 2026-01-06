package bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bookstore.bean.BooksBean;
import bookstore.bean.GenreBean;
import bookstore.dao.impl.bookService;
import bookstore.exceptionCenter.BusinessException;

@Controller
@RequestMapping("/books")
public class BookController {

	@Autowired
	private bookService bookService;
	
	
	@GetMapping("/booksIndex")
	public String bookIndex(Model model) {
		return"books/booksIndex";
	}

	@GetMapping("/getAllBooks")
	public String getAllBooks(Model model) {
		List<BooksBean> bookList = bookService.selectAllBooks();
		model.addAttribute("bookList", bookList);
		return "books/GetAllBooks";
	}

	@GetMapping("/getBook")
	public String selectBookById(@RequestParam("id") String idStr, Model model) {
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
	public String insertBook(@ModelAttribute BooksBean book, RedirectAttributes ra,
			@RequestParam("mainImage") MultipartFile file) {
		BooksBean insertBook = bookService.insertBook(book, file);
		ra.addFlashAttribute("insertBook", insertBook);
		ra.addFlashAttribute("msg", "書籍新增成功!");
		return "redirect:/books/getAllBooks";
	}

	@GetMapping("/insertSuccess")
	public String insertSuccess() {
		return "books/InsertSuccess";
	}

	@GetMapping("/updatePage")
	public String updatePage(@RequestParam("id") Integer id, Model model) {
		List<GenreBean> genreList = bookService.getAllGenres();
		model.addAttribute("genreList", genreList);

		BooksBean book = bookService.selectBookByIdS(id);
		model.addAttribute("book", book);
		return "books/UpdateBook";
	}

	@PostMapping("/update")
	public String updateBook(@ModelAttribute BooksBean book, @RequestParam(required = false) Integer genreId,
			RedirectAttributes ra) {
		bookService.updateBook(book, genreId);
		ra.addFlashAttribute("status", "success");
		ra.addFlashAttribute("msg", "資料更新成功");
		return "redirect:/books/getAllBooks";
	}

	@PostMapping("/updateStatus")
	public String updateOnShelf(@RequestParam("id") Integer id, @RequestParam("status") boolean status) {
		bookService.updateOnShelfStatus(id, status);
		return "redirect:/books/getAllBooks";
	}

	@PostMapping("/delete")
	public String deleteBook(@RequestParam("id") Integer id, RedirectAttributes ra) {
		bookService.deleteBookById(id);
		ra.addFlashAttribute("status", "success");
		ra.addFlashAttribute("msg", "書籍已成功刪除");
		return "redirect:/books/getAllBooks";
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
