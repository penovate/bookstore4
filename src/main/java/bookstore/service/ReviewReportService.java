package bookstore.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bookstore.bean.BooksBean;
import bookstore.bean.ReviewBean;
import bookstore.bean.ReviewReportBean;
import bookstore.bean.UserBean;
import bookstore.dto.ReportList;
import bookstore.repository.BookRepository;
import bookstore.repository.ReviewReportRepository;
import bookstore.repository.ReviewRepository;
import bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewReportService {

    @Autowired
    private ReviewReportRepository reportRepo;
    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BookRepository bookRepo;

    // 查詢全部
    // public List<ReviewReportBean> findAllReports(){
    // return reviewReportRepository.findAll();
    // }

    // 查詢單筆
    public ReviewReportBean findById(Integer id) {
        return reportRepo.findById(id).orElse(null);
    }

    // 新增 修改
    public ReviewReportBean save(ReviewReportBean report) {
        return reportRepo.save(report);
    }

    // 刪除
    // public void delete(Integer reviewId) {
    // reviewReportRepository.deleteById(reviewId);
    // }

    // 檢查是否存在
    public boolean hasReported(Integer userId, Integer reviewId) {
        return reportRepo.existsByUserIdAndReviewId(userId, reviewId);
    }

    public List<ReportList> getAllReportsForAdmin() {
        List<ReviewReportBean> reports = reportRepo.findAll();
        List<ReportList> dtoList = new ArrayList<>();

        for (ReviewReportBean report : reports) {
            // --- A. 找檢舉人名字 ---
            String reporterName = "未知用戶";
            Optional<UserBean> userOpt = userRepo.findById(report.getUserId());
            if (userOpt.isPresent()) {
                reporterName = userOpt.get().getUserName();
            }

            // --- B. 找書籍與評論內容 ---
            String bookTitle = "未知書籍";
            String fullContent = "評論已刪除";
            String reviewContent = "評論已刪除";

            Optional<ReviewBean> reviewOpt = reviewRepo.findById(report.getReviewId());
            if (reviewOpt.isPresent()) {
                ReviewBean review = reviewOpt.get();
                fullContent = review.getComment();

                // 摘要：只取前 20 個字，超過加 "..."
                if (fullContent != null) {
                    reviewContent = fullContent.length() > 20 ? fullContent.substring(0, 20) + "..." : fullContent;
                }

                // 查書名
                Optional<BooksBean> bookOpt = bookRepo.findById(review.getBookId());
                if (bookOpt.isPresent()) {
                    bookTitle = bookOpt.get().getBookName(); // 假設 BookBean 有 getBookName()
                }
            }

            // 0:待處理, 1:成立(Hidden), 2:駁回(Public)
            Integer statusInt = 0; // 預設待處理
            String dbStatus = report.getStatus();
            if ("已成立".equals(dbStatus)) {
                statusInt = 1;
            } else if ("已駁回".equals(dbStatus)) {
                statusInt = 2;
            }

            // --- D. 組裝 DTO ---
            ReportList dto = new ReportList(
                    report.getReviewReportId(), // 假設 PK 是 reportId
                    reporterName,
                    bookTitle,
                    reviewContent,
                    fullContent,
                    report.getReason(),
                    statusInt,
                    report.getCreatedAt());

            dtoList.add(dto);
        }
        return dtoList;
    }

    
    // 2. 處理審核 (更新檢舉單 + 隱藏/顯示評論)
     
    @Transactional
    public void processReport(Integer reportId, Integer newStatus) {
        ReviewReportBean report = reportRepo.findById(reportId)
                .orElseThrow(() -> new RuntimeException("找不到檢舉紀錄 ID: " + reportId));

        // 根據前端傳來的數字，決定後端要存什麼狀態字串
        if (newStatus == 1) {
            // === 檢舉成立 ===
            report.setStatus("已成立");
            
            report.setProcessedAt(LocalDateTime.now());
            report.setAdminResults("已下架隱藏。");

            // 把該則評論隱藏 (status = 0)
            Optional<ReviewBean> reviewOpt = reviewRepo.findById(report.getReviewId());
            if (reviewOpt.isPresent()) {
                ReviewBean review = reviewOpt.get();
                review.setStatus(0); // 假設 ReviewBean 的 0 代表隱藏
                reviewRepo.save(review);
            }

        } else if (newStatus == 2) {
            // === 檢舉駁回 ===
            report.setStatus("已駁回");
            report.setProcessedAt(LocalDateTime.now());
            report.setAdminResults("維持顯示。");

            // 確保評論是顯示的 (status = 1) 
            Optional<ReviewBean> reviewOpt = reviewRepo.findById(report.getReviewId());            
            if (reviewOpt.isPresent()) {
                ReviewBean review = reviewOpt.get();
                // 只有原本是被隱藏的才需要恢復，避免誤觸其他邏輯
                if (review.getStatus() == 0) {
                    review.setStatus(1);
                    reviewRepo.save(review);
                }
            }
        } else {
            report.setStatus("待處理");
        }

        reportRepo.save(report);
    }

}
