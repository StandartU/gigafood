// userService.js
import { apiRequest } from "../base_request.js";

export const UserService = {
    redactUser: (data, headers) => apiRequest({ path: "/user/redact", method: "POST", body: data, headers: headers  }),
    getUserData: (headers) => apiRequest({ path: "/user/get", method: "GET", headers: headers  }),
};
