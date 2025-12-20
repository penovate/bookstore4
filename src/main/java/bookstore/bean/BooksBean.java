package bookstore.bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BooksBean {

	
	private Integer bookId; //序號
	private String bookName; //書名
	private String author; //作者
	private String translator; //譯者
	private Integer genre; //書本類型編號
	private BigDecimal price; //價錢
	private String isbn; //書本身分證
	private Integer stock; //庫存量
	private String shortDesc; //簡述
	private LocalDateTime createdAt; //建立時間
	private String press; //出版社
	private boolean onShelf; //上下架狀態
	private String genreName; //類型名稱
	
	
	
	
	
	//-------Constructor--------
	public BooksBean() {
		super();
		
	
		
	}
	public BooksBean(Integer bookId, String bookName, String author, String translator, Integer genre, BigDecimal price,
			Integer stock, String shortDesc, LocalDateTime createdAt, String press, String isbn,boolean onShelf,String genreName) {
		super();
		this.bookId = bookId;
		this.bookName = bookName;
		this.author = author;
		this.translator = translator;
		this.genre = genre;
		this.price = price;
		this.stock = stock;
		this.shortDesc = shortDesc;
		this.createdAt = createdAt;
		this.press = press;
		this.isbn = isbn;
		this.onShelf = onShelf;
		this.genreName = genreName;
	}
	//--------getter/setter-------
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
	public Integer getGenre() {
		return genre;
	}
	public void setGenre(Integer genres) {
		this.genre = genres;
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
	
	public String getGenreName() {
		return genreName;
	}
	public void setGenresName(String genreName) {
		this.genreName = genreName;
	}
	@Override
	public String toString() {
		return "BooksBean [bookId=" + bookId + ", bookName=" + bookName + ", author=" + author + ", translator="
				+ translator + ", genres=" + genre + ", price=" + price + ", isbn=" + isbn + ", stock=" + stock
				+ ", shortDesc=" + shortDesc + ", createdAt=" + createdAt + ", press=" + press + ", onShelf=" + onShelf
				+ "]";
	}
	
	
}
