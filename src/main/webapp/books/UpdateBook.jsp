<%@page import="java.util.List"%>
<%@page import="bookstore.bean.GenreBean"%>
<%@page import="bookstore.bean.BooksBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
BooksBean book = (BooksBean) request.getAttribute("book");
List<GenreBean> genresList = (List<GenreBean>) request.getAttribute("genresList");

if (book == null) {
	response.sendRedirect("BookList"); // 或者其他錯誤處理
	return;
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>修改書籍資料 - <%=book.getBookName()%></title>

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
    margin-bottom: 20px;
}

td {
	padding: 12px 10px;
	text-align: right;
	font-weight: 500;
	color: #6d6d6d;
}

/* 讓輸入欄位靠左對齊 */
td:nth-child(2) {
    text-align: left;
}


/* 輸入框與下拉式選單共用樣式 */
td input, td select {
	width: 100%;
	padding: 10px 12px;
	font-size: 15px;
	border: 1px solid #d0c8b9; 
	border-radius: 4px;
	background: #fefcf9; 
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

/* 唯讀欄位樣式 */
.read-only {
	background-color: #f7f3e8 !important; /* 淺米色背景 */
	cursor: default;
	color: #7b5e47; /* 棕色文字 */
	font-weight: bold;
	border: 1px solid #dcd5c7;
}

/* ------------------ 按鈕樣式 (主要動作) ------------------ */
.btn-container {
	margin-top: 25px;
	display: flex;
	justify-content: center;
	gap: 15px;
}

.btn {
	padding: 10px 20px;
	font-size: 16px;
	font-weight: bold;
	border: none;
	border-radius: 4px;
	color: white;
	cursor: pointer;
	letter-spacing: 0.1em;
	transition: background-color 0.2s, transform 0.2s, box-shadow 0.2s;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.btn-submit {
	background: #a07d58; /* 皮革棕 */
}

/* hover 效果 */
.btn-submit:hover {
	background: #926f4e;
	box-shadow: 0 6px 15px rgba(0, 0, 0, 0.25);
	transform: translateY(-2px);
}

/* active 效果 */
.btn-submit:active {
	transform: translateY(0);
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
}

/* 這裡可以加上一個取消按鈕，使用輔助色 (若不需要，請忽略) */
.btn-back {
    background-color: #e8e4dc; 
    color: #4a4a4a;
    font-weight: normal;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); 
}

.btn-back:hover {
    background-color: #dcd5c7;
    transform: translateY(-1px); 
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.btn-back:active {
    background-color: #dcd5c7;
    transform: translateY(0);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>

</head>
<body>

	<div class="card">
		<h2>修改書籍資料：<%=book.getBookName()%></h2>
		<form method="post" action="UpdateBook">
			<table>

				<input type="hidden" name="bookId" value="<%=book.getBookId()%>">

				<tr>
					<td>書籍編號 (ID):</td>
					<td><input type="text" value="<%=book.getBookId()%>" disabled class="read-only"></td>
				</tr>

				<tr>
					<td>書籍名稱:</td>
					<td><input type="text" name="bookName"value="<%=book.getBookName()%>" required></td>
				</tr>
				<tr>
					<td>作者:</td>
					<td><input type="text" name="author" value="<%=book.getAuthor()%>" required></td>
				</tr>
				<tr>
					<td>譯者:</td>
					<td><input type="text" name="translator" value="<%=book.getTranslator() == null ? "" : book.getTranslator()%>"></td>
				</tr>
				<tr>
					<td>類型:</td>
					<td><select name="genre" required>
							<option value="" disabled>-- 請選擇書籍類型 --</option>
							<%
							if (genresList != null) {
								for (GenreBean genre : genresList) {
									// 取得當前書籍的類型 ID
									Integer currentGenreId = book.getGenre();
									// 判斷是否為當前書籍的類型，若是則設定 selected 屬性
									String selected = (genre.getGenreId().equals(currentGenreId)) ? "selected" : "";
							%>
							<option value="<%=genre.getGenreId()%>" <%=selected%>><%=genre.getGenreName()%></option>
							<%
							}
							}
							%>
					</select></td>
				</tr>
				<tr>
					<td>出版社:</td>
					<td><input type="text" name="press"	value="<%=book.getPress()%>" required></td>
				</tr>
				<tr>
					<td>價錢:</td>
					<td><input type="text" name="price"	value="<%=book.getPrice()%>" required pattern="[0-9]+(\.[0-9]{1,2})?"></td>
				</tr>
				<tr>
					<td>ISBN:</td>
					<td><input type="text" name="isbn" value="<%=book.getIsbn()%>" required></td>
				</tr>
				<tr>
					<td>庫存:</td>
					<td><input type="text" name="stock" value="<%=book.getStock()%>" required pattern="[0-9]+"></td>
				</tr>
				<tr>
					<td>簡述:</td>
					<td><input type="text" name="short_desc" value="<%=book.getShortDesc() == null ? "" : book.getShortDesc()%>"></td>
				</tr>
				<tr>
					<td>上下架狀態:</td>
					<td><select name="on_shelf" required>
							<option value="true" <%=book.getOnShelf() ? "selected" : ""%>>上架
								(True)</option>
							<option value="false" <%=!book.getOnShelf() ? "selected" : ""%>>下架
								(False)</option>
					</select></td>
				</tr>
			</table>

			<div class="btn-container">
				<input type="submit" class="btn btn-submit" value="確認修改">
				<button type="button" class="btn btn-back" onclick="window.location.href='GetAllBooks'">取消並返回列表</button>
			</div>
		</form>
	</div>

</body>
</html>