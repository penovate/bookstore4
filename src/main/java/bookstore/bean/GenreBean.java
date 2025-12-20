package bookstore.bean;

public class GenreBean {

	private Integer genreId;
	private String genreName;

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
