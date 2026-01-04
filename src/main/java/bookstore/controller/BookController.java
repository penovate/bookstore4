package bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bookstore.bean.BooksBean;
import bookstore.dao.impl.bookService;
import bookstore.exceptionCenter.BusinessException;

@Controller
@RequestMapping("/books")
public class BookController {

	@Autowired
	private bookService bookService;
	@ResponseBody
	@GetMapping("/getAllBooks")
	public List<BooksBean> getAllBooks(Model model) {
		List<BooksBean> bookList = bookService.selectAllBooks();
		model.addAttribute("getAllBooks", bookList);
		return bookList;
	}

	@ResponseBody
	@GetMapping("/getBook")
	public String selectBookById(@RequestParam("id") String idStr, Model model) {
		if (idStr == null || !idStr.matches("^\\d+$")) {
			throw new BusinessException(400, "格式錯誤:ID必須為數字");
		}
		BooksBean book = bookService.selectBookByIdS(Integer.parseInt(idStr));
		model.addAttribute("book", book);
		return "books/GetBook";
	}

	@ResponseBody
	@GetMapping("/InsertPage")
	public String InsertPage() {
		return "books/InsertBook";
	}

	@PostMapping("/insert")
	public String insertBook(@ModelAttribute BooksBean book,
			RedirectAttributes ra,
			@RequestParam("mainImage") MultipartFile file) {
		bookService.insertBook(book,file);
		ra.addFlashAttribute("msg", "書籍新增成功!");
		return "redirect:/books/insertSuccess";
	}

	@ResponseBody
	@GetMapping("/insertSuccess")
	public String insertSuccess() {
		return "books/InsertSuccess";
	}

	@ResponseBody
	@GetMapping("/updatePage")
	public String updatePage(@RequestParam("id") Integer id, Model model) {
		BooksBean book = bookService.selectBookByIdS(id);
		model.addAttribute("book", book);
		return "books/UpdateBook";
	}

	@ResponseBody
	@PostMapping("/update")
	public String updateBook(@ModelAttribute BooksBean book,@RequestParam(required = false) Integer genreId ,RedirectAttributes ra) {
		bookService.updateBook(book,genreId);
		ra.addFlashAttribute("status", "success");
		ra.addFlashAttribute("msg", "資料更新成功");
		return "redirect:/books/getAllBooks";
	}
	
	@ResponseBody
	@PostMapping("/updateStatus")
	public String updateOnShelf(@RequestParam("id") Integer id, @RequestParam("status") boolean status) {
		bookService.updateOnShelfStatus(id, status);
		return "redirect:books/getAllBooks";
	}
	
	@ResponseBody
	@PostMapping("/delete")
	public String deleteBook(@RequestParam("id") Integer id ,RedirectAttributes ra) {
		bookService.deleteBookById(id);
		ra.addFlashAttribute("status","success");
		ra.addFlashAttribute("msg","書籍已成功刪除");
		return "redirect:/books/getAllBooks";
	}

}
