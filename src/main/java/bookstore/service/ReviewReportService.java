package bookstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bookstore.bean.ReviewReportBean;
import bookstore.repository.ReviewReportRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewReportService {

	@Autowired
	private ReviewReportRepository reviewReportRepository;

	// 查詢全部	
	public  List<ReviewReportBean> findAllReports(){
		return reviewReportRepository.findAll();
	}
	
	//	查詢單筆
	public ReviewReportBean findById(Integer id) {
		return reviewReportRepository.findById(id).orElse(null);
	}
	
	//	新增 修改	
	public ReviewReportBean save(ReviewReportBean report) {
		return reviewReportRepository.save(report);
	}
	
	//	刪除	
	public void delete(Integer reviewId) {
		reviewReportRepository.deleteById(reviewId);
	}
	
}
