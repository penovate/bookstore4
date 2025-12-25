<%@page import="java.util.List"%>
<%@page import="bookstore.bean.GenreBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
List<GenreBean> genreList = (List<GenreBean>) request.getAttribute("genreList");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>新增書籍資料</title>

<style>
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

/* 中央白色卡片 */
.card {
	width: 90%;
	max-width: 850px; /* 擴大卡片寬度以容納表格 */
	padding: 35px 45px;
	box-sizing: border-box;
	border: 1px solid #dcd5c7;
	border-radius: 6px;
	background-color: #ffffff;
	box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
	text-align: center;
}

/* 標題 */
h2 {
	color: #7b5e47;
	font-size: 26px;
	margin-top: 0;
	margin-bottom: 30px;
	padding-bottom: 15px;
	border-bottom: 1px solid #e0d9c9;
	font-weight: bold;
}

/* ------------------ 表格與欄位樣式 ------------------ */
table {
	width: 100%;
	border-collapse: collapse;
	font-size: 15px;
}

td {
	padding: 12px 10px;
	text-align: right;
	font-weight: 500;
	color: #6d6d6d; /* 標籤文字顏色 */
}

/* 右側輸入欄位容器 */
.card table tr td:nth-child(2) {
	text-align: left;
	display: flex;
	flex-direction: column;
	align-items: flex-start;
}

.requirement {
	color: red;
	display: block;
	font-size: 13px;
	font-weight: normal;
	height: 18px; /* 確保佔位，避免輸入框跳動 */
	margin-bottom: 3px;
}

/* 輸入框與下拉式選單共用樣式 */
td input, td select {
	width: 100%;
	padding: 10px 12px;
	font-size: 15px;
	border: 1px solid #d0c8b9;
	border-radius: 4px;
	background: #fefcf9; /* 淺背景色 */
	transition: 0.15s;
	box-sizing: border-box;
	height: 40px;
	color: #4a4a4a;
}

td input:focus, td select:focus {
	border-color: #9fb89e; /* 青綠色 focus */
	box-shadow: 0 0 4px rgba(159, 184, 158, .5);
	outline: none;
}

/* ------------------ 按鈕樣式 (主要動作) ------------------ */
.btn-submit {
	margin-top: 30px;
	padding: 12px 30px;
	font-size: 17px;
	font-weight: bold;
	letter-spacing: 0.1em;
	/* 主要動作色：皮革棕 */
	background: #a07d58;
	border: none;
	border-radius: 4px;
	color: white;
	cursor: pointer;
	transition: background-color 0.2s, transform 0.2s, box-shadow 0.2s;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.btn-submit:hover {
	background: #926f4e;
	box-shadow: 0 6px 15px rgba(0, 0, 0, 0.25);
	transform: translateY(-2px);
}

.btn-submit:active {
	background: #926f4e;
	transform: translateY(0);
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
}
</style>

</head>
<body>

	<div class="card">
		<h2>請輸入書籍資料</h2>

		<form method="post" action="InsertBook">
			<table>


				<tr>
					<td>書籍名稱:</td>
					<td><span id="nameSpan" class="requirement">*</span> <input
						type="text" name="bookName" id="bookName" required placeholder="請輸入書籍名稱.."></td>
				</tr>
				<tr>
					<td>作者:</td>
					<td><span id="authorSpan" class="requirement">*</span> <input
						type="text" name="author" id="author" required placeholder="請輸入書籍作者.."></td>
				</tr>
				<tr>
					<td>譯者:</td>
					<td><span id="translatorSpan" class="requirement">*</span> <input
						type="text" name="translator" id="translator" required placeholder="請輸入書籍譯者.."></td>
				</tr>

				<tr>
					<td>類型:</td>
					<td><span id="genreSpan" class="requirement">*</span> <select
						name="genre" id="genre" required>
							<option value="" disabled selected>-- 請選擇書籍類型 --</option>
							<%
							if (genreList != null && !genreList.isEmpty()) {
								for (GenreBean genre : genreList) {
							%>
							<option value="<%=genre.getGenreId()%>"><%=genre.getGenreName()%></option>
							<%
							}
							} else {
							%>
							<option value="" disabled>無可用類型資料</option>
							<%
							}
							%>
					</select></td>
				</tr>
				<tr>
					<td>出版社:</td>
					<td><span id="pressSpan" class="requirement">*</span> <input
						type="text" name="press" required id="press" placeholder="請輸入書籍出版社.."></td>
				</tr>
				<tr>
					<td>價錢:</td>
					<td><span id="priceSpan" class="requirement">*</span> <input
						type="text" name="price" required id="price" placeholder="請輸入價錢.."></td>
				</tr>
				<tr>
					<td>ISBN:</td>
					<td><span id="isbnSpan" class="requirement">*</span> <input
						type="text" name="isbn" maxlength="13" id="isbn" required placeholder="請輸入書13位數字.."></td>
				</tr>
				<tr>
					<td>庫存:</td>
					<td><span id="stockSpan" class="requirement">*</span> <input
						type="text" name="stock" required id="stock" placeholder="請輸入庫存量..">
				</tr>
				<tr>
					<td>簡述:</td>
					<td><span id="shortDescSpan" class="requirement">*</span> <input
						type="text" name="short_desc" id="shortDesc" required placeholder="請輸入書籍描述..">
				</tr>
			</table>
			<input type="submit" class="btn-submit" value="送出">
		</form>
	</div>
	<script src="https://code.jquery.com/jquery-3.6.0.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

	<!-- JQ資料驗證 -->
	<script>
		//booksName Check Function	
		function nameCheck() {
			let content = $(this).val().trim();
			if (content.length === 0) {
				$('#nameSpan').text('書名不可為空白！').css('color', 'red');
			} else {
				$('#nameSpan').text('✓').css('color', 'green')
			}
		}

		$('#bookName').on('input', nameCheck);

		//isbn Check function
		function isbnCheck() {
		    const rule1 = /^[0-9]+$/;
		    const content = $(this).val(); // 建議統一使用 content
		    const $span = $('#isbnSpan');

		    if (content.length == 0) {
		        $span.text('ISBN不可空白').css('color', 'red');
		    } else if (!rule1.test(content)) {
		        $span.text('ISBN只能為數字').css('color', 'red');
		    } else if (content.length < 13) {
		        $span.text('ISBN必須為13碼數字').css('color', 'red');
		    } else {
		        // --- 只有以上格式都通過 (滿13碼且全數字)，才執行 Ajax ---
		        $.ajax({
		            url: '<%=request.getContextPath()%>/isbnCheck',
					type : 'post',
					dataType : 'text',
					data : {
						isbn : content
					// 修正：這裡要用 content，而不是 value
					},
					success : function(e) {
						if (e == "false") {
							$span.text('✓').css('color', 'green');
						} else {
							$span.text('此ISBN已存在').css('color', 'red');
						}
					}
				}); // Ajax 結束
			} // else 結束
		}
		$('#isbn').on('input', isbnCheck);

		//genre Check function
		function genreCheck() {
			let content = $(this).val()
			if (content == null) {
				$('#genreSapn').text('請選擇類型').css('color', 'red')
			} else {
				$('#genreSpan').text('✓').css('color', 'green')
			}
		}
		$('#genre').on('change', genreCheck)

		//price Check Function
		function priceCheck() {
			let content = $(this).val()
			let rule1 = /[0-9]/
			if (content.length < 0) {
				$('#priceSpan').text('請輸入價格').css('color', 'red')
			} else if (content.length >= 5) {
				$('#priceSpan').text('你是要搶劫還是不想做生意?').css('color', 'red')
			} else if (!rule1.test(content)) {
				$('#priceSpan').text('只能輸入數字').css('color', 'red')
			} else {
				$('#priceSpan').text('✓').css('color', 'green')
			}
		}
		$('#price').on('input', priceCheck)
		//author Check Function
		function authorCheck() {
			let content = $(this).val()
			if (content.length == 0) {
				$('#authorSpan').text('作者不可為空白').css('color', 'red')
			} else {
				$('#authorSpan').text('✓').css('color', 'green')
			}
		}
		$('#author').on('input', authorCheck)

		//press Check Function
		function pressCheck() {
			let content = $(this).val()
			if (content.length == 0) {
				$('#pressSpan').text('作者不可為空白').css('color', 'red')
			} else {
				$('#pressSpan').text('✓').css('color', 'green')
			}
		}
		$('#press').on('input', pressCheck)

		//stock Check Function
		function stockCheck() {
			let content = $(this).val()
			let rule1 = /[0-9]/
			if (content.length == 0) {
				$('#stockSpan').text('請輸入庫存').css('color', 'red')
			} else if (!rule1.test(content)) {
				$('#stockSpan').text('只能輸入數字').css('color', 'red')
			} else {
				$('#stockSpan').text('✓').css('color', 'green')
			}
		}
		$('#stock').on('input', stockCheck)

		//translator Check function
		function translatorCheck() {
			let content = $(this).val();
			if (content.length >= 0) {
				$('#translatorSpan').text('✓').css('color', 'green')
			}
		}
		$('#translator').on('input', translatorCheck)

		//shortDesc Check function
		function shortDescCheck() {
			let content = $(this).val()
			if (content >= 0) {
				$('#shortDescSpan').text('✓').css('color', 'green')
			}
		}
		$('#shortDesc').on('input', shortDescCheck)

		//insert表單送出檢查
		$('.btn-submit').on(
				'click',
				function(e) {
					let form = $(this).closest('form')
					let nameVal = $('#nameSpan').text();
					let authorVal = $('#authorSpan').text();
					let translatorVal = $('#translatorSpan').text();
					let genreVal = $('#genreSpan').text();
					let pressVal = $('#pressSpan').text();
					let priceVal = $('#priceSpan').text();
					let isbnVal = $('#isbnSpan').text();
					let stockVal = $('#stockSpan').text();
					let shortDescVal = $('#shortDescSpan').text();
					if (nameVal == '✓' && authorVal == '✓'
							&& translatorVal == '✓' && genreVal == '✓'
							&& pressVal == '✓' && priceVal == '✓'
							&& isbnVal == '✓' && stockVal == '✓'
							&& shortDescVal == '✓') {
						form.submit();
					} else {
						e.preventDefault();
						Swal.fire({
							icon : "error",
							title : "無法送出",
							text : "請確認欄位內容皆填寫正確。",
						});

					}

				})

	</script>


</body>
</html>