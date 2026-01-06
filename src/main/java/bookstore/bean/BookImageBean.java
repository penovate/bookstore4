package bookstore.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "book_images")
public class BookImageBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Integer imageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private BooksBean book;

	@Column(name = "image_url", length = 265)
	private String imageUrl;

	@Column(name = "is_main")
	private Boolean isMain;

	public BookImageBean() {
		super();
	}

	public BookImageBean(BooksBean book, String imageUrl, Boolean isMain) {
		super();
		this.book = book;
		this.imageUrl = imageUrl;
		this.isMain = isMain;
	}

	public BookImageBean(Integer imageId, BooksBean book, String imageUrl, Boolean isMain) {
		super();
		this.imageId = imageId;
		this.book = book;
		this.imageUrl = imageUrl;
		this.isMain = isMain;
	}

	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public BooksBean getBook() {
		return book;
	}

	public void setBook(BooksBean book) {
		this.book = book;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Boolean getIsMain() {
		return isMain;
	}

	public void setIsMain(Boolean isMain) {
		this.isMain = isMain;
	}

}
