package bookstore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bookstore.bean.BooksBean;
import bookstore.bean.LogItemBean;
import bookstore.bean.StockLogBean;
import bookstore.exceptionCenter.BusinessException;
import bookstore.repository.BookRepository;
import bookstore.repository.StockLogRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StockLogService {
	@Autowired
	private StockLogRepository stockLogRepository;

	@Autowired
	private BookRepository bookRepository;

	public List<StockLogBean> getAllStockLogs() {
		List<StockLogBean> stockLogList = stockLogRepository.findAll();
		if (stockLogList.isEmpty()) {
			 log.warn("查無任何貨單"); // Consider removing warning for normal empty state
		}
		log.info("查詢貨單成功，取得 {} 筆資料", stockLogList.size());
		return stockLogList;
	}

	public StockLogBean getStock(Integer logId) {
		if (logId == null) {
			log.warn("查詢失敗 - 貨單ID不可為空白");
			throw new BusinessException(400, "貨單ID不可為空白");
		}
		Optional<StockLogBean> opt = stockLogRepository.findById(logId);
		if (opt.isPresent()) {
			log.info("查詢成功 - 取得ID:{} 貨單資料", opt.get().getLogId());
			return opt.get();
		}
		throw new BusinessException(404, "查無ID" + logId + "相關資料");
	}

	@Transactional
	public StockLogBean insertStockLog(StockLogBean stockLogBean) {
		// 1. 設定日期
		if (stockLogBean.getLogTime() == null) {
			stockLogBean.setLogTime(new java.sql.Date(System.currentTimeMillis()));
		}

		BigDecimal totalAmount = BigDecimal.ZERO;
		List<LogItemBean> items = stockLogBean.getLogItemBeans();

		if (items != null) {
			for (LogItemBean item : items) {
				// 建立關聯
				item.setStockLogBean(stockLogBean);

				// 計算金額 (成本 * 數量)
				Integer qty = item.getChangeQty() != null ? item.getChangeQty() : 0;
				BigDecimal subTotal = item.getCostPrice().multiply(BigDecimal.valueOf(qty));
				totalAmount = totalAmount.add(subTotal);

				// 處理庫存
				if (item.getBooksBean() != null && item.getBooksBean().getBookId() != null) {
					Integer bookId = item.getBooksBean().getBookId();
					Optional<BooksBean> bookOpt = bookRepository.findById(bookId);

					if (bookOpt.isPresent()) {
						BooksBean book = bookOpt.get();
						item.setBooksBean(book); // 確保關聯到完整的 Entity

						Integer currentStock = book.getStock();
						Integer changeQty = item.getChangeQty() != null ? item.getChangeQty() : 0;
						Integer newStock = currentStock;

						// stockType 1: 進貨 (加), 2: 退貨 (減)
						if (stockLogBean.getStockType() == 1) {
							newStock = currentStock + changeQty;
						} else if (stockLogBean.getStockType() == 2) {
							newStock = currentStock - changeQty;
							if (newStock < 0) {
								throw new BusinessException(400, "書籍 " + book.getBookName() + " 庫存不足，無法退貨");
							}
						}

						book.setStock(newStock);
						bookRepository.save(book);
					} else {
						throw new BusinessException(404, "查無書籍ID: " + bookId);
					}
				}
			}
		}

		stockLogBean.setTotalAmount(totalAmount);
		return stockLogRepository.save(stockLogBean);
	}

}
