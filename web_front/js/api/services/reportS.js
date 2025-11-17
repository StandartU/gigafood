// reportService.js
import { apiRequest } from "../base_request.js";

export const ReportService = {
    weekReport: (headers) => apiRequest({ path: "/report/week", method: "GET", headers: headers  }),
    dayReport: (headers) => apiRequest({ path: "/report/day", method: "GET", headers: headers  }),
};
