package bookstore.bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "reviews" })
@NoArgsConstructor
@AllArgsConstructor
public class BooksBean {

	@Id
	@Column(name = "book_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bookId; // 序號

//	@NotBlank(message = "書名不可為空白")
//	@Size(max = 50, message = "書名長度不可超過50字")
	@Column(name = "book_name")
	private String bookName; // 書名

//	@NotBlank(message = "作者不可為空白")
	@Column(name = "author")
	private String author; // 作者

	@Column(name = "translator")
	private String translator; // 譯者

//	@NotBlank(message = "價錢不可為空白")
//	@DecimalMin(value = "0.0",inclusive = true,message = "價格不能小0且不可為負數")
	@Column(name = "price")
	private BigDecimal price; // 價錢

//	@NotBlank(message = "ISBN不可為空白")
//	@Pattern(regexp = "^\\d{13}$",message = "格式驗證失敗，必須為13位數字")
	@Column(name = "isbn")
	private String isbn; // 書本身分證

//	@NotNull(message = "庫存量不可為空")
//	@Min(value = 0,message = "庫存量不可小於0")
	@Column(name = "stock")
	private Integer stock; // 庫存量

	@Column(name = "short_desc")
	private String shortDesc; // 簡述

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt; // 建立時間

//	@NotBlank(message = "出版社不可為空白")
	@Column(name = "press")
	private String press; // 出版社

	@Column(name = "on_shelf")
	private Integer onShelf = 0; // 上下架狀態

	@ManyToMany
	@JoinTable(name = "book_genre_map", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
	@JsonIgnoreProperties("books")
	private Set<GenreBean> genres = new HashSet<GenreBean>();

	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<ReviewBean> reviews;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<BookImageBean> imageList = new ArrayList<BookImageBean>();

	public void addImage(BookImageBean image) {
		imageList.add(image);
		image.setBook(this);
	}

	// -------Constructor--------

	public BooksBean(Integer bookId, String bookName, String author, String translator, BigDecimal price, Integer stock,
			String shortDesc, LocalDateTime createdAt, String press, String isbn, Integer onShelf) {
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
			String shortDesc, String press, Integer onShelf, GenreBean genreBean) {
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
	}

	// --------getter/setter-------
	public Integer getBookId() {
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

	public Integer getStock() {
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

	public Integer getOnShelf() {
		return onShelf;
	}

	public void setOnShelf(Integer onShelf) {
		this.onShelf = onShelf;
	}

	public List<ReviewBean> getReviews() {
		return reviews;
	}

	public void setReviews(List<ReviewBean> reviews) {
		this.reviews = reviews;
	}

	public Set<GenreBean> getGenres() {
		return genres;
	}

	public void setGenres(Set<GenreBean> genres) {
		this.genres = genres;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null || getClass() != object.getClass())
			return false;
		BooksBean that = (BooksBean) object;
		return Objects.equals(bookId, that.bookId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bookId);
	}
}
