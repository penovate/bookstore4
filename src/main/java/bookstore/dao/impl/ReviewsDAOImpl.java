package bookstore.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bookstore.bean.ReviewBean;
import bookstore.dao.ReviewDAO;
import bookstore.util.DBUtil;

public class ReviewsDAOImpl implements ReviewDAO {
//	查詢所有評價
	@Override
	public List<ReviewBean> selectAllReviews() {
		List<ReviewBean> reviews = new ArrayList<>();
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql =
				    "SELECT r.review_id, r.user_id, u.user_name, r.book_id, b.book_name, " +
				    "r.rating, r.comment, FORMAT(r.created_at, 'yyyy-MM-dd HH:mm:ss') AS created_at " +
				    "FROM reviews r " +
				    "JOIN users u ON r.user_id = u.user_id " +
				    "JOIN books b ON r.book_id = b.book_id";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ReviewBean review = new ReviewBean();
				review.setReviewId(rs.getString("review_id"));
				review.setUserId(rs.getString("user_id"));
				review.setUserName(rs.getString("user_name"));
				review.setBookId(rs.getString("book_id"));
				review.setBookName(rs.getString("book_name"));
				review.setRating(rs.getString("rating"));
				review.setComment(rs.getString("comment"));
				review.setCreatedAt(rs.getString("created_at"));
                reviews.add(review);
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close(); 
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return reviews;
	}

//  透過評價id查詢評價
	@Override
	public ReviewBean selectReviewById(String reviewId) {
		ReviewBean review = null;
		Connection conn = null;
		
		try {
			conn = DBUtil.getConnection();
			
	        String sql =
	                "SELECT r.review_id, r.user_id, u.user_name, r.book_id, b.book_name, " +
	                "r.rating, r.comment, FORMAT(r.created_at, 'yyyy-MM-dd HH:mm:ss') AS created_at " +
	                "FROM reviews r " +
	                "JOIN users u ON r.user_id = u.user_id " +
	                "JOIN books b ON r.book_id = b.book_id " +
	                "WHERE r.review_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, reviewId);
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				review = new ReviewBean();
				review.setReviewId(rs.getString("review_id"));
				review.setUserId(rs.getString("user_id"));
				review.setUserName(rs.getString("user_name"));
				review.setBookId(rs.getString("book_id"));
				review.setBookName(rs.getString("book_name"));
				review.setRating(rs.getString("rating"));
				review.setComment(rs.getString("comment"));
				review.setCreatedAt(rs.getString("created_at"));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
                if (conn != null) { conn.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
		}
		return review;
	}
//  新增評價
	@Override
	public int insertReview(ReviewBean review) {
		int count = 0;
		Connection conn = null;
		
		String sql = "INSERT INTO reviews (user_id, book_id, rating, comment) VALUES (?, ?, ?, ?)";
		try {
			conn = DBUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, review.getUserId());
			stmt.setString(2, review.getBookId());
	        stmt.setString(3, review.getRating());
	        stmt.setString(4, review.getComment());
	        
	        count = stmt.executeUpdate();
	        stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
                if (conn != null) { conn.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
		}
		return count;
		
		
	}
//  更新評價
	@Override
	public int updateReview(ReviewBean review) {
		int count = 0;
		Connection conn = null;
		String sql ="UPDATE reviews SET user_id = ?, book_id = ?, rating = ?, comment = ? WHERE review_id = ?";
		try {
			conn = DBUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setString(1, review.getUserId());
			stmt.setString(2, review.getBookId());
	        stmt.setString(3, review.getRating());
	        stmt.setString(4, review.getComment());
	        stmt.setString(5, review.getReviewId());
            count = stmt.executeUpdate();
            stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
                if (conn != null) { conn.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
		}
		return count;
	}
//  刪除評價
	@Override
	public int deleteReview(String reviewId) {
		int count = 0;
		Connection conn = null;
		
		String sql ="DELETE FROM reviews WHERE review_id = ?";
		try {
			conn = DBUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, reviewId);
			count = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
                if (conn != null) { conn.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
		}
		return count;
	}

}
