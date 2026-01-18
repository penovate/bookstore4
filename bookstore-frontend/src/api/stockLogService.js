import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const apiClient = axios.create({
    baseURL: API_URL + '/log',
    headers: {
        'Content-Type': 'application/json',
    },
});

export default {
    getAllStockLogs() {
        return apiClient.get('/getAllStockLogs');
    },
    getStockLog(logId) {
        return apiClient.get('/getStockLog', { params: { logid: logId } });
    },
    insertStockLog(stockLogData) {
        return apiClient.post('/insert', stockLogData);
    },
    updateStockLog(stockLogData) {
        return apiClient.post('/update', stockLogData);
    },
    returnStockLog(logId) {
        return apiClient.post('/return', { logId });
    }
};
