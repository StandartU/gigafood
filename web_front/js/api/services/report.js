// reportService.js
import { apiRequest } from "./apiService.js";

export const ReportService = {
    weekReport: () => apiRequest({ path: "/report/week", method: "GET" }),
    dayReport: () => apiRequest({ path: "/report/day", method: "GET" }),
};
