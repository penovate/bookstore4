package bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bookstore.bean.BooksBean;
import bookstore.dao.impl.bookService;

@Controller
@RequestMapping("/books")
public class BookController {

	@Autowired
	private bookService bookService;

	@GetMapping("/getAllBooks")
	public String getAllBooks(Model model) {
		model.addAttribute("allbooks", bookService.selectAllBooks());
		return "books/getAllBooks";
	}
	
	public String InsertBook(@ModelAttribute("book") BooksBean book,RedirectAttributes ra) {
	}

}
