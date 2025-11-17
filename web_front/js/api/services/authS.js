// authService.js
import { apiRequest } from "../base_request.js";

export const AuthService = {
    signup: (data) => apiRequest({ path: "/auth/signup", method: "POST", body: data }),
    login: (data) => apiRequest({ path: "/auth/login", method: "POST", body: data }),
    refreshToken: (headers) => apiRequest({ path: "/auth/token/refresh", method: "GET", headers: headers }),
};
