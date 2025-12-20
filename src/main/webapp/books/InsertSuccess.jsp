<%@page import="bookstore.bean.BooksBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>修改結果</title>
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
    max-width: 600px; /* 調整卡片寬度 */
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

tr {
    border-bottom: 1px dashed #e0d9c9;
}

tr:last-child {
    border-bottom: none;
}

td {
	padding: 12px 0;
	text-align: right;
	font-weight: 500;
	color: #6d6d6d;
}

/* 確保輸入框在第二欄靠左 */
.card table tr td:nth-child(2) {
	text-align: left;
}

/* ------------------ 輸入框樣式 ------------------ */

/* 唯讀輸入框樣式 */
td input {
	width: 100%;
	padding: 10px 12px;
	font-size: 15px;
	border: 1px solid #dcd5c7;
	border-radius: 4px;
	background-color: #f7f3e8; /* 淺米色背景 */
	color: #4a4a4a;
	font-weight: bold;
	height: 40px;
	cursor: default;
	box-sizing: border-box;
}

/* ------------------ 按鈕樣式 (輔助動作) ------------------ */
.btn-container {
    margin-top: 20px;
}

input[type="submit"] {
    width: 50%;
    max-width: 250px;
    height: 45px;
    padding: 12px 0;
    box-sizing: border-box;
    
    /* 輔助動作樣式 */
    background-color: #e8e4dc; /* 淺土色 */
    color: #4a4a4a;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    font-weight: normal;
    letter-spacing: 0.1em;
    
    cursor: pointer;
    transition: background-color 0.2s, transform 0.2s, box-shadow 0.2s;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* hover 效果 */
input[type="submit"]:hover {
    background: #dcd5c7;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    transform: translateY(-1px);
}

/* 按下效果 */
input[type="submit"]:active {
    background: #dcd5c7;
    transform: translateY(0);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>
</head>
<body>

	<div class="card">
		<h2>書籍新增完成，資料如下</h2>
		<jsp:useBean id="bookInsert" scope="request" class="bookstore.bean.BooksBean" />
		<table>
			<tr>
				<td>書名:</td>
				<td><input type="text" disabled value="${bookInsert.bookName }"></td>
			</tr>
			<tr>
				<td>作者:</td>
				<td><input type="text" disabled value="${bookInsert.author }"></td>
			</tr>
			<tr>
				<td>譯者:</td>
				<td><input type="text" disabled
					value="${bookInsert.translator }"></td>
			</tr>
			<tr>
				<td>類型:</td>
				<td><c:set var="currentGenreName" value="未知類型" /> <c:forEach
						var="genre" items="${genreList}">
						<c:if test="${genre.genreId == bookInsert.genre}">
							<c:set var="currentGenreName" value="${genre.genreName}" />


						</c:if>
					</c:forEach> <input type="text" disabled value="${currentGenreName}"></td>
			</tr>
			<tr>
				<td>出版社:</td>
				<td><input type="text" disabled value="${bookInsert.press }"></td>
			</tr>
			<tr>
				<td>價錢:</td>
				<td><input type="text" disabled value="${bookInsert.price }"></td>
			</tr>
			<tr>
				<td>ISBN:</td>
				<td><input type="text" disabled value="${bookInsert.isbn }"></td>
			</tr>
			<tr>
				<td>庫存:</td>
				<td><input type="text" disabled value="${bookInsert.stock }"></td>
			</tr>
			<tr>
				<td>簡述:</td>
				<td><input type="text" disabled
					value="${bookInsert.shortDesc }"></td>
			</tr>
		</table>
		<div class="btn-container">
			<form action="GetAllBooks" method="get">
				<input type="submit" value="查看全部書籍" class="btn-submit">
			</form>
		</div>
	</div>

</body>
</html>