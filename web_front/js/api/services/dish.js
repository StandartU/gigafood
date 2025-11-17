// dishService.js
import { apiRequest } from "./apiService.js";

export const DishService = {
    analyze: (data) => apiRequest({ path: "/dish/analyze", method: "POST", body: data }),
    getDish: (uuid) => apiRequest({ path: `/dish/get/${uuid}`, method: "POST" }),
    redactDish: (uuid, data) => apiRequest({ path: `/dish/redact/${uuid}`, method: "POST", body: data }),
    getAllDishes: () => apiRequest({ path: "/dish/all", method: "POST" }),
    getPhoto: (photoUrl) => apiRequest({ path: `/dish/get_photo/${photoUrl}`, method: "POST" }),
};
