package bookstore.bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
@DynamicInsert
@DynamicUpdate
public class BooksBean {

	@Id
	@Column(name = "book_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bookId; // 序號

	@Column(name = "book_name")
	private String bookName; // 書名

	@Column(name = "author")
	private String author; // 作者

	@Column(name = "translator")
	private String translator; // 譯者

	@Column(name = "price")
	private BigDecimal price; // 價錢

	@Column(name = "isbn")
	private String isbn; // 書本身分證

	@Column(name = "stock")
	private Integer stock; // 庫存量

	@Column(name = "short_desc")
	private String shortDesc; // 簡述

	@Column(name = "created_at")
	private LocalDateTime createdAt; // 建立時間

	@Column(name = "press")
	private String press; // 出版社

	@Column(name = "on_shelf")
	private boolean onShelf; // 上下架狀態

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "genre_id")
	private GenreBean genreBean;

	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
	private List<ReviewBean> reviews;
	
	// -------Constructor--------
	public BooksBean() {

	}

	public BooksBean(Integer bookId, String bookName, String author, String translator, BigDecimal price, Integer stock,
			String shortDesc, LocalDateTime createdAt, String press, String isbn, boolean onShelf) {
		super();
		this.bookId = bookId;
		this.bookName = bookName;
		this.author = author;
		this.translator = translator;
		this.price = price;
		this.stock = stock;
		this.shortDesc = shortDesc;
		this.createdAt = createdAt;
		this.press = press;
		this.isbn = isbn;
		this.onShelf = onShelf;
	}

	public BooksBean(String bookName, String author, String translator, BigDecimal price, String isbn, Integer stock,
			String shortDesc, String press, boolean onShelf, GenreBean genreBean) {
		super();
		this.bookName = bookName;
		this.author = author;
		this.translator = translator;
		this.price = price;
		this.isbn = isbn;
		this.stock = stock;
		this.shortDesc = shortDesc;
		this.press = press;
		this.onShelf = onShelf;
		this.genreBean = genreBean;
	}

	public BooksBean(Integer bookId, String bookName, String author, String translator, BigDecimal price, String isbn,
			Integer stock, String shortDesc, String press, GenreBean genreBean) {
		super();
		this.bookId = bookId;
		this.bookName = bookName;
		this.author = author;
		this.translator = translator;
		this.price = price;
		this.isbn = isbn;
		this.stock = stock;
		this.shortDesc = shortDesc;
		this.press = press;
		this.genreBean = genreBean;
	}

	// --------getter/setter-------
	public int getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTranslator() {
		return translator;
	}

	public void setTranslator(String translator) {
		this.translator = translator;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getPress() {
		return press;
	}

	public void setPress(String press) {
		this.press = press;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public boolean getOnShelf() {
		return onShelf;
	}

	public void setOnShelf(boolean onShelf) {
		this.onShelf = onShelf;
	}

	public GenreBean getGenreBean() {
		return genreBean;
	}

	public void setGenreBean(GenreBean genreBean) {
		this.genreBean = genreBean;
	}
	
	public List<ReviewBean> getReviews() {
		return reviews;
	}

	public void setReviews(List<ReviewBean> reviews) {
		this.reviews = reviews;
	}

	@Override
	public String toString() {
		return "BooksBean [bookId=" + bookId + ", bookName=" + bookName + ", author=" + author + ", translator="
				+ translator + ", price=" + price + ", isbn=" + isbn + ", stock=" + stock + ", shortDesc=" + shortDesc
				+ ", createdAt=" + createdAt + ", press=" + press + ", onShelf=" + onShelf + "]";
	}

}
