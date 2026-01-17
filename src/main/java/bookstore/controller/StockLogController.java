package bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bookstore.bean.StockLogBean;
import bookstore.service.StockLogService;

@RestController
@RequestMapping("/log")
public class StockLogController {

	@Autowired
	private StockLogService stockLogService;

	@GetMapping("/getAllStockLogs")
	public List<StockLogBean> getAllStockLogs() {
		List<StockLogBean> stockLogBeans = stockLogService.getAllStockLogs();
		return stockLogBeans;
	}

	@GetMapping("/getStockLog")
	public ResponseEntity<StockLogBean> getStockLog(Integer logid) {
		StockLogBean result = stockLogService.getStock(logid);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/insert")
	public ResponseEntity<StockLogBean> insertStockLog(@RequestBody StockLogBean stockLogBean) {
		StockLogBean result = stockLogService.insertStockLog(stockLogBean);
		return ResponseEntity.ok(result);
	}

}
