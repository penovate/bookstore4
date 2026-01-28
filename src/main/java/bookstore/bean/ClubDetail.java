package bookstore.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "club_detail")
public class ClubDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "clubdetail_id")
	private Integer clubDetailId;
	
	@OneToOne
	private BookClubsBean mainClub;
	
	@Column(name = "purpose")
	private String purpose;

	@Column(name = "diffult_level")
	private Integer diffultLevel;
	
	@Column(name = "agenda")
	private String agenda;
	
	@Column(name = "mode_type")
	private Integer modeType;
	
	@Column(name = "requirement")
	private String requirement;
	
	@Column(name = "proof_path")
	private String proofPath;
}
