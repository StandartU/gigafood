// dishService.js
import { apiRequest, apiFileReq } from "../base_request.js";

export const DishService = {
    analyze: (file, headers) => apiRequest({ path: "/dish/analyze", method: "POST", file: file, headers: headers  }),
    getDish: (uuid, headers) => apiRequest({ path: `/dish/get/${uuid}`, method: "POST", headers: headers  }),
    redactDish: (uuid, data, headers) => apiRequest({ path: `/dish/redact/${uuid}`, method: "POST", body: data, headers: headers  }),
    getAllDishes: (headers) => apiRequest({ path: "/dish/all", method: "POST", headers: headers  }),
    getPhoto: (photoUrl, headers) => apiFileReq({ path: `/dish/get_photo/${photoUrl}`, method: "POST", headers: headers  }),
};
