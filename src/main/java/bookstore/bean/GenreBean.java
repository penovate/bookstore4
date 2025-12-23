package bookstore.bean;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "genres")
public class GenreBean {
	@Id
	@Column(name = "genre_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer genreId;

	@Column(name = "genre_name")
	private String genreName;

	@OneToMany(mappedBy = "genreBean")
	private List<BooksBean> books;

	// Constructor----
	public GenreBean() {
		super();
	}

	public GenreBean(Integer genreId, String genreName) {
		super();
		this.genreId = genreId;
		this.genreName = genreName;
	}

	// Getter/Setter----
	public Integer getGenreId() {
		return genreId;
	}

	public void setGenreId(Integer genreId) {
		this.genreId = genreId;
	}

	public String getGenreName() {
		return genreName;
	}

	public void setGenreName(String genreName) {
		this.genreName = genreName;
	}

	@Override
	public String toString() {
		return "GenreBean [genreId=" + genreId + ", genreName=" + genreName + "]";
	}

}
