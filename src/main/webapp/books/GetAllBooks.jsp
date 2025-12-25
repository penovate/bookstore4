<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="bookstore.bean.BooksBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="https://cdn.datatables.net/2.0.7/css/dataTables.dataTables.min.css">
<meta charset="UTF-8">
<title>書籍資料</title>

<style>
/* ------------------ 基礎與頁面佈局 ------------------ */
body {
	font-family: '微軟正黑體', 'Arial', sans-serif;
	background-color: #fcf8f0;
	color: #4a4a4a;
	margin: 0;
	padding: 40px 0;
	display: flex;
	justify-content: center;
	align-items: flex-start;
	min-height: 100vh;
}

#mainDiv {
	width: 95%;
	max-width: 1800px; /* 擴大最大寬度 */
	padding: 30px 24px;
	box-sizing: border-box;
	border: 1px solid #dcd5c7;
	border-radius: 6px;
	background-color: #ffffff;
	box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
	text-align: center;
	position: relative;
}

/* 標題 */
.h2-st1 {
	color: #7b5e47;
	font-size: 26px;
	margin: 0 0 25px 0;
	padding-bottom: 15px;
	border-bottom: 1px solid #e0d9c9;
	font-weight: bold;
}

/* 底部總計文字 */
h3 {
	color: #9c8470;
	font-weight: normal;
	margin-top: 30px;
	margin-bottom: 20px;
}

/* 頂部操作欄 */
#action-bar {
	width: 100%;
	margin-top: 15px;
	margin-bottom: 25px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

/* ------------------ 表格樣式 ------------------ */
table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 10px;
	font-size: 14px;
}

th, td {
	border: 1px solid #e0d9c9;
	padding: 10px 8px;
	vertical-align: middle;
}

th {
	background-color: #e8e4dc; /* 標題背景色 */
	color: #5d5d5d;
	font-weight: bold;
	letter-spacing: 0.5px;
	white-space: nowrap;
}

/* 表格主體 */
.books-table tbody tr:nth-child(even) {
	background-color: #f7f3f0;
}

.books-table tbody tr:hover {
	background-color: #fffaf0;
	transition: background-color 0.3s;
}

/* 連結 */
td a {
	color: #a07d58;
	text-decoration: none;
	transition: color 0.3s;
}

td a:hover {
	color: #7b5e47;
	text-decoration: underline;
}

/* 特殊對齊 */

/* ------------------ 按鈕樣式 & 動態效果 ------------------ */
.btn, .btn-insert, input[type="submit"], .back-to-index-button {
	padding: 8px 15px;
	border-radius: 4px;
	border: none;
	cursor: pointer;
	color: white;
	font-size: 14px;
	font-weight: 500;
	transition: background-color 0.2s, transform 0.1s, box-shadow 0.2s;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

table button {
	margin: 0 3px;
	display: inline-block;
}

/* 頂部 新增書籍資料按鈕 (Primary: 皮革棕) */
.btn-insert, input[value="新增書籍資料"] {
	background-color: #a07d58;
	color: white;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
	font-weight: bold;
	font-size: 16px;
	height: 40px;
}

.btn-insert:hover, input[value="新增書籍資料"]:hover {
	background-color: #926f4e;
	transform: translateY(-2px);
	box-shadow: 0 6px 12px rgba(0, 0, 0, 0.25);
}

.btn-insert:active, input[value="新增書籍資料"]:active {
	transform: translateY(0);
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
}

/* 頂部 批量刪除按鈕 (Warning: 柔和土紅) */
.btn-deleteAll {
	background-color: #d89696;
	color: white;
	font-weight: bold;
}

.btn-deleteAll:hover {
	background-color: #c48383;
	transform: translateY(-1px);
	box-shadow: 0 3px 6px rgba(0, 0, 0, 0.15);
}

.btn-deleteAll:active {
	background-color: #c48383;
	transform: translateY(0);
	box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

#batchDeleteBtn[disabled] {
	opacity: 0.5;
	cursor: not-allowed;
	transform: none;
	box-shadow: none;
}

/* 表格內 修改按鈕 (Secondary: 青綠色) */
.btn-edit, input[value="修改"] {
	background-color: #9fb89e;
	color: #4a4a4a;
}

.btn-edit:hover, input[value="修改"]:hover {
	background-color: #8da68c;
	transform: translateY(-1px);
	box-shadow: 0 3px 6px rgba(0, 0, 0, 0.15);
}

.btn-edit:active, input[value="修改"]:active {
	transform: translateY(0);
	box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 表格內 刪除按鈕 (Warning: 柔和土紅) */
.btn-delete, input[value="刪除"] {
	background-color: #d89696;
	color: white;
}

.btn-delete:hover, input[value="刪除"]:hover {
	background-color: #c48383;
	transform: translateY(-1px);
	box-shadow: 0 3px 6px rgba(0, 0, 0, 0.15);
}

.btn-delete:active, input[value="刪除"]:active {
	background-color: #c48383;
	transform: translateY(0);
	box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 底部 返回書籍資料處理首頁按鈕 */
.back-to-index-button {
	height: 40px;
	padding: 10px 20px;
	font-size: 16px;
	font-weight: normal;
	background-color: #e8e4dc;
	color: #4a4a4a;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.back-to-index-button:hover {
	background-color: #dcd5c7 !important;
	transform: translateY(-1px);
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.back-to-index-button:active {
	background-color: #dcd5c7 !important;
	transform: translateY(0);
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* ------------------ 彈窗樣式覆蓋 (SweetAlert2) ------------------ */
.swal2-popup {
	background-color: #fffaf0 !important;
	border-radius: 8px !important;
	box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2) !important;
}

.swal2-title {
	color: #7b5e47 !important;
	font-size: 20px !important;
}

.swal2-content {
	color: #4a4a4a !important;
}

.swal2-success .swal2-success-ring {
	border-color: #9fb89e !important;
}

/* ------------------ 其他細節修正 ------------------ */
/* Checkbox checked style */
.books-table tr {
	transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

.selected {
	background-color: #e8e4dc;
	box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.2);
}

.swal2-container {
	position: fixed !important;
	top: 0 !important;
	left: 0 !important;
	right: 0 !important;
	bottom: 0 !important;
	z-index: 1060 !important;
}
/* --- 矩形 Switch 樣式 --- */
.switch-container {
	display: flex;
	flex-direction: column; /* 文字放在開關下方或並排 */
	align-items: center;
	gap: 4px;
}

.book-switch {
	position: relative;
	display: inline-block;
	width: 50px; /* 長方形寬度 */
	height: 24px;
}

.book-switch input {
	opacity: 0;
	width: 0;
	height: 0;
}

.slider {
	position: absolute;
	cursor: pointer;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: #dcd5c7; /* 關閉時的顏色 (淺土色) */
	transition: .3s;
	border-radius: 4px; /* 矩形圓角，符合整體按鈕風格 */
}

.slider:before {
	position: absolute;
	content: "";
	height: 18px;
	width: 18px;
	left: 4px;
	bottom: 3px;
	background-color: white;
	transition: .3s;
	border-radius: 2px; /* 內鈕也採矩形設計 */
}

/* 開啟狀態：皮革棕 */
input:checked+.slider {
	background-color: #a07d58;
}

/* 點擊移動距離 */
input:checked+.slider:before {
	transform: translateX(24px);
}

/* 狀態文字樣式 */
.status-label {
	font-size: 12px;
	color: #7b5e47;
	font-weight: bold;
}
</style>

</head>
<body>
	<div id="mainDiv">
		<h2 class="h2-st1">書籍資料</h2>

		<div id="action-bar">

			<form action="InsertBook" method="get">
				<input type="submit" value="新增書籍資料" class="btn-insert">
			</form>
			<!-- 
			<form action="DeleteBook" method="post" id="mainBatchForm">
				<input type="submit" value="批量刪除 (0)" class="btn btn-deleteAll"
					id="batchDeleteBtn" disabled>
			</form>
 -->

		</div>
		<table class="books-table">
			<thead>
				<tr>
					<!-- 
					<th style="text-align: center;"><input type="checkbox"
						id="checkAll"></th>
				 -->

					<th style="text-align: center;">書籍編號</th>
					<th style="text-align: left;">書籍名稱</th>
					<th style="text-align: center;">作者</th>
					<th style="text-align: center;">譯者</th>
					<th style="text-align: center;">出版社</th>
					<th style="text-align: center;">類型</th>
					<th style="text-align: center;">價錢</th>
					<th style="text-align: center;">ISBN</th>
					<th style="text-align: center;">庫存</th>
					<th style="text-align: center;">修改</th>
					<th style="text-align: center;">刪除</th>
					<th style="text-align: center;">銷售狀態</th>
					<!-- 
					<th style="text-align: center;">狀態</th>
					 -->
				</tr>
			</thead>

			<tbody>
				<%
				List<BooksBean> booksList = (ArrayList<BooksBean>) request.getAttribute("booksList");
				%>
				<%
				if (booksList != null) {
					for (BooksBean book : booksList) {
				%>
				<tr>
					<!-- 
					<td style="text-align: center;"><input type="checkbox"
						class="checkOne" name="selectedBookId"
						value="<%=book.getBookId()%>" form="mainBatchForm"></td>
				 -->
					<td style="text-align: center;"><%=book.getBookId()%></td>
					<td style="text-align: left;"><a
						href="GetBook?bookId=<%=book.getBookId()%>"><%=book.getBookName()%></a></td>
					<td style="text-align: center;"><%=book.getAuthor()%></td>
					<td style="text-align: center;"><%=(book.getTranslator() == null) ? "" : book.getTranslator()%></td>
					<td style="text-align: center;"><%=book.getPress()%></td>
					<td style="text-align: center;">
						<%
						if (book.getGenreBean() != null) {
							out.print(book.getGenreBean().getGenreName());
						} else {
							out.print("未分類");
						}
						%>
					</td>
					<td style="text-align: center;"><%=book.getPrice().intValue()%></td>
					<td style="text-align: center;"><%=book.getIsbn()%></td>
					<td style="text-align: center;"><%=book.getStock()%></td>
					<!-- 
					<td style="text-align: center;"><%=(book.getOnShelf() == true) ? "上架中" : "下架中"%></td>
					 -->
					<td style="text-align: center;">
						<form action="UpdateBook" method="get">
							<input type="hidden" name="bookId" value="<%=book.getBookId()%>">
							<input type="submit" value="修改" class="btn btn-edit">
						</form>
					</td>
					<td style="text-align: center;">
						<form action="DeleteBook" method="post">
							<input type="hidden" name="bookId" value="<%=book.getBookId()%>">
							<input type="submit" value="刪除" class="btn btn-delete">
						</form>
					</td>
					<td style="text-align: center;">
						<div class="switch-container">
							<label class="book-switch"> <input type="checkbox"
								class="on-shelf-toggle" data-bookid="<%=book.getBookId()%>"
								<%=(book.getOnShelf()) ? "checked" : ""%>> <span
								class="slider"></span>
							</label> <span class="status-label"> <%=(book.getOnShelf()) ? "上架中" : "下架中"%>
							</span>
						</div>
					</td>
				</tr>
				<%
				}
				} else {
				%>
				<tr>
					<td colspan="11">目前沒有任何書籍資料</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>
		<h3>
			共
			<%=booksList.size()%>
			筆資料
		</h3>
		<a href="books/booksIndex.html"><button
				class="back-to-index-button">返回書籍資料處理首頁</button></a>

	</div>
	<script src="https://code.jquery.com/jquery-3.6.0.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script src="https://cdn.datatables.net/2.0.7/js/dataTables.min.js"></script>

	<script>
const CONFIRM_COLOR = "#d89696"; // 警告色 (柔和土紅)
const CANCEL_COLOR = "#e8e4dc"; // 輔助色 (淺土色)
//DataTable
$(document).ready(function () {
    $('.books-table').DataTable({
    	"columnDefs": [
            {
                "targets": [ 0 ], 
                "orderable": false 
            }
        ],
        "language": {
            "lengthMenu": "顯示 _MENU_ 項",
            "zeroRecords": "沒有符合的結果",
            "info": "第 _START_ 至 _END_ 項",
            "infoEmpty": "顯示第 0 至 0 項結果，共 0 項",
            "infoFiltered": "(從 _MAX_ 項結果中過濾)",
            "search": "搜索:",
            "paginate": {
                "first": "首頁",
                "previous": "上一頁",
                "next": "下一頁",
                "last": "末頁"
            }
        }
    });
});

$(document).ready(function() {
    // 監聽上下架 Switch 的變更事件 
    $(document).on('change', '.on-shelf-toggle', function() {
        const checkbox = $(this);
        const bookId = checkbox.data('bookid');
        const isChecked = checkbox.is(':checked'); 
        const statusLabel = checkbox.closest('.switch-container').find('.status-label');
        const actionText = isChecked ? "上架" : "下架";

        checkbox.prop('checked', !isChecked);

        Swal.fire({
            title:'確認要將此書籍' + actionText + '嗎？',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#a07d58', 
            cancelButtonColor: '#e8e4dc',
            confirmButtonText: '確定',
            cancelButtonText: '取消'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: '<%=request.getContextPath()%>/UpdateShelfStatus', // 您的 Servlet 路徑
                    type: 'POST',
                    data: { 
                        bookId: bookId, 
                        status: isChecked 
                    },
                    success: function(res) {
                        if (res.trim() === "success") {
                            checkbox.prop('checked', isChecked);
                            statusLabel.text(isChecked ? "上架中" : "下架中");

                            Swal.fire({
                                icon: 'success',
                                title: `${actionText}成功`,
                                timer: 1000,
                                showConfirmButton: false
                            });
                        } else {
                            Swal.fire('失敗', '更新狀態失敗，請聯繫管理員', 'error');
                        }
                    },
                    error: function() {
                        Swal.fire('錯誤', '伺服器通訊失敗', 'error');
                    }
                });
            }
        });
    });
});

/*
$(document).ready(function() {
	//被選取的資料修改backGround	
	$('.checkOne').on('change', function() {
        var row = $(this).closest('tr');
        if (this.checked) {
            row.addClass('selected');
        } else {
            row.removeClass('selected');
        }        
        updateCheckAllStatus();
    });

	
   	//選取全部資料and修改backGround	
	$('#checkAll').on('change', function() {
        var isChecked = this.checked;
        $('.checkOne').each(function() {
            this.checked = isChecked;
            var row = $(this).closest('tr');
            if (isChecked) {
                row.addClass('selected');
            } else {
                row.removeClass('selected');
            }
        });
        updateCheckAllStatus(); 
    });

	//動態檢視按鈕狀態
	
   	function updateCheckAllStatus() {
        var singleChecks = $('.checkOne'); 
        var checkedChecks = $('.checkOne:checked');
        var checkedCount = checkedChecks.length; // 取得被勾選的數量
        var batchBtn = $('#batchDeleteBtn'); // 取得批量刪除按鈕
        var checkAll = $('#checkAll'); 

		//控制全選Checkbox顯示 全選樣式=打勾、有選但沒有全部=槓槓
        if (singleChecks.length === checkedCount && singleChecks.length > 0) {
            checkAll.prop('checked', true).prop('indeterminate', false);
        } else if (checkedCount > 0) {
            checkAll.prop('checked', false).prop('indeterminate', true); 
        } else {
            checkAll.prop('checked', false).prop('indeterminate', false);
        }
        
        if (checkedCount > 0) {
            batchBtn.prop('disabled', false); 
        } else {
            batchBtn.prop('disabled', true); 
        }
        batchBtn.val('批量刪除 (' + checkedCount + ')');
    }
    updateCheckAllStatus();

});
*/
</script>

	<script>
	
	//單筆刪除檢查
	$('.btn-delete').on('click', function(e) {
	    e.preventDefault(); 
	    
	    const btn = $(this);
	    const form = btn.closest('form');
	    const bookId = form.find('input[name=bookId]').val();
	    const row = btn.closest('tr');
	    
	    const onShelfStatus = row.find('.status-label').text().trim();

	    if (onShelfStatus === '上架中') {
	        Swal.fire({
	            icon: 'error',
	            title: '無法刪除',
	            text: '書籍目前為「上架中」，請先將其下架。'
	        });
	        return; 
	    }

	    $.ajax({
	        url: '<%=request.getContextPath()%>/bookDeleteCheck', 
	        type: 'POST',
	        data: { bookId: bookId },
	        success: function(allCheck) {
	            if (allCheck.trim() === "false") {
	                Swal.fire({
	                    icon: 'error',
	                    title: '無法刪除',
	                    text: '此書籍已有關聯的訂單或評價資料，不可刪除。'
	                });
	            } else {
	                Swal.fire({
	                    title: '確認刪除這筆資料？',
	                    text: '此操作將永久刪除且無法復原！',
	                    icon: 'warning',
	                    showCancelButton: true,
	                    confirmButtonColor: '#d89696', 
	                    cancelButtonColor: '#e8e4dc',
	                    confirmButtonText: '確認刪除',
	                    cancelButtonText: '取消'
	                }).then((result) => {
	                    if (result.isConfirmed) {
	                        form.submit();
	                    }
	                });
	            }
	        },
	        error: function() {
	            Swal.fire('錯誤', '系統檢查失敗，請稍後再試', 'error');
	        }
	    });
	});

	
	// 修改按鈕防呆檢查
	$('.btn-edit').on('click', function(e) {
	    const btn = $(this);
	    const row = btn.closest('tr');
	    
	    const onShelfStatus = row.find('.status-label').text().trim();

	    if (onShelfStatus === '上架中') {
	        e.preventDefault(); 
	        
	        Swal.fire({
	            icon: 'error',
	            title: '無法修改',
	            text: '書籍目前處於「上架中」狀態，請先將其下架後再進行修改。',
	            confirmButtonColor: '#a07d58' // 皮革棕
	        });
	    }
	});
</script>

	<script>
	



let deleteStatus = "<%=request.getParameter("deleteStatus")%>"
		if (deleteStatus == "success")
			Swal.fire({
				icon : "success",
				title : "已刪除",
				text : "資料已成功刪除。",
			});
	</script>


</body>
</html>