import axios from 'axios';

const axiosClient = axios.create({
    baseURL: "https://localhost:8080/api/v1",
});

// const TOKEN_KEY = "auth-token";

axiosClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default axiosClient;
