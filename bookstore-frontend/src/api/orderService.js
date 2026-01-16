/* src/api/orderService.js */
import axios from 'axios';

// API 基礎 URL
const ORDER_API_URL = 'http://localhost:8080/order'; // 原組件使用的端點
const CART_API_URL = 'http://localhost:8080/cart';   // 原組件使用的端點
const BOOKS_API_URL = 'http://localhost:8080/books'; // 用於 BookStore 的 getAllBooks

const apiClient = axios.create({
    baseURL: 'http://localhost:8080', // 使用根 URL 以處理混合端點
    withCredentials: true,
});

// 取得 JWT 標頭的輔助函式
const getAuthHeaders = () => {
    const token = localStorage.getItem('userToken');
    return {
        headers: {
            'Authorization': token ? `Bearer ${token}` : '',
        }
    };
};

export default {
    // --- 購物車操作 ---

    // 未使用或舊版？通常購物車是根據 session/token 針對每個使用者的
    // getCartItems() { ... } 

    // 取得購物車項目
    getCartItems() {
        return apiClient.get('/cart/api/items', getAuthHeaders());
    },

    // 加入購物車
    addToCart(bookId, quantity) {
        const params = new URLSearchParams();
        params.append('bookId', bookId);
        params.append('quantity', quantity);
        return apiClient.post('/cart/api/add', params, getAuthHeaders());
    },

    // 更新購物車數量
    updateCartItem(cartId, quantity) {
        const params = new URLSearchParams();
        params.append('cartId', cartId);
        params.append('quantity', quantity);
        return apiClient.post('/cart/api/update', params, getAuthHeaders());
    },

    // 從購物車移除
    removeCartItem(cartId) {
        const params = new URLSearchParams();
        params.append('cartId', cartId);
        return apiClient.post('/cart/api/remove', params, getAuthHeaders());
    },

    // --- 訂單操作 (後台) ---

    // 取得所有訂單 (後台篩選)
    getAllOrders(filter = 'Active') {
        // 後端針對活動與已取消訂單有不同的端點
        const endpoint = filter === 'Cancelled' ? '/order/api/cancelledList' : '/order/api/activeList';
        return apiClient.get(endpoint, getAuthHeaders());
    },

    // 新增多筆項目到訂單
    addOrderItems(orderId, items) {
        const params = new URLSearchParams();
        params.append('orderId', orderId);
        items.forEach(item => {
            params.append('bookId', item.bookId);
            params.append('quantity', item.quantity);
            params.append('price', item.price);
        });
        return apiClient.post('/order/api/addItems', params, getAuthHeaders());
    },

    // 取得訂單詳情
    getOrderDetail(orderId) {
        return apiClient.get('/order/api/get', {
            params: { id: orderId },
            ...getAuthHeaders()
        });
    },

    // 建立訂單 (手動新增)
    createOrder(orderData, orderItems) {
        const params = new URLSearchParams();
        params.append('userId', orderData.userId);
        params.append('recipientName', orderData.recipientName);
        params.append('phone', orderData.phone);
        params.append('address', orderData.address);
        params.append('deliveryMethod', orderData.deliveryMethod);
        params.append('paymentMethod', orderData.paymentMethod);

        orderItems.forEach(item => {
            params.append('bookId', item.bookId);
            params.append('quantity', item.quantity);
            params.append('price', item.price);
        });

        return apiClient.post('/order/api/insert', params, getAuthHeaders());
    },

    // 取消訂單
    cancelOrder(orderId) {
        const params = new URLSearchParams();
        params.append('id', orderId);
        return apiClient.post('/order/api/cancel', params, getAuthHeaders());
    },

    // 還原訂單
    restoreOrder(orderId) {
        const params = new URLSearchParams();
        params.append('id', orderId);
        return apiClient.post('/order/api/restore', params, getAuthHeaders());
    },

    // --- 訂單項目管理 ---

    // 新增項目到訂單
    addOrderItem(orderId, bookId, price, quantity) {
        const params = new URLSearchParams();
        params.append('orderId', orderId);
        params.append('bookId', bookId);
        params.append('price', price);
        params.append('quantity', quantity);
        return apiClient.post('/order/api/addItems', params, getAuthHeaders());
    },

    // 更新訂單項目
    updateOrderItem(orderId, orderItemId, price, quantity, bookId = null) {
        const params = new URLSearchParams();
        params.append('orderId', orderId);
        params.append('orderItemId', orderItemId);
        params.append('price', price);
        params.append('quantity', quantity);
        if (bookId) params.append('booksBean.bookId', bookId);

        return apiClient.post('/order/api/updateItem', params, getAuthHeaders());
    },

    // 刪除訂單項目
    deleteOrderItem(orderId, orderItemId) {
        const params = new URLSearchParams();
        params.append('orderId', orderId);
        params.append('orderItemId', orderItemId);
        return apiClient.post('/order/api/deleteItem', params, getAuthHeaders());
    },

    // --- 書籍資訊輔助 (用於訂單新增/加入) ---
    getBookInfo(bookId) {
        return apiClient.get(`/order/getBookDetail`, {
            params: { bookId: bookId },
            ...getAuthHeaders()
        });
    },

    // 結帳 (從購物車)
    checkout(checkoutData) {
        return apiClient.post('/orders/checkout', checkoutData, getAuthHeaders());
    },

    // --- 書城 (前台) ---
    getAllBooks() {
        return apiClient.get('/books/getAllBooks', getAuthHeaders());
    }
};
