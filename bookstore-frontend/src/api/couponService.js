import axios from 'axios';

const API_URL = 'http://localhost:8080/api/coupons';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
});

const getAuthHeaders = () => {
    const token = localStorage.getItem('userToken');
    return {
        headers: {
            'Authorization': token ? `Bearer ${token}` : '',
        }
    };
};

export default {
    claimCoupon(code) {
        return apiClient.post(`${API_URL}/claim`, { code }, getAuthHeaders());
    },

    getMyCoupons() {
        return apiClient.get(`${API_URL}/my`, getAuthHeaders());
    },

    getAllCouponsAdmin() {
        return apiClient.get(`${API_URL}/admin/all`, getAuthHeaders());
    }
};
