// authService.js
import { apiRequest } from "./apiService.js";

export const AuthService = {
    signup: (data) => apiRequest({ path: "/auth/signup", method: "POST", body: data }),
    login: (data) => apiRequest({ path: "/auth/login", method: "POST", body: data }),
    refreshToken: () => apiRequest({ path: "/auth/token/refresh", method: "GET" }),
};
