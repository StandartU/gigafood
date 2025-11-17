// userService.js
import { apiRequest } from "./apiService.js";

export const UserService = {
    redactUser: (data) => apiRequest({ path: "/user/redact", method: "POST", body: data }),
    getUserData: () => apiRequest({ path: "/user/get", method: "GET" }),
};
