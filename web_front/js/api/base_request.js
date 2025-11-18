const BASE_URL = "http://localhost:8080/gigafood/api/v1";

export async function apiRequest({ path, method = "GET", body = null, file = null, headers = {} }) {
    const url = `${BASE_URL}${path}`;
    let fetchOptions = { method, headers: { ...headers } };

    console.log("API Request →", method, url, {
        body: file ? "(FormData)" : body,
        headers: fetchOptions.headers
    }); 

    if (file) {
        // Если есть файл — формируем FormData
        const formData = new FormData();
        formData.append("file", file);

        // Если есть другие данные в body — тоже добавляем
        if (body && typeof body === "object") {
            for (const key in body) {
                formData.append(key, body[key]);
            }
        }

        fetchOptions.body = formData;
        console.log("API Request →", method, url, "(sending FormData)", file, body);
    } else if (body) {
        // JSON
        fetchOptions.body = JSON.stringify(body);
        fetchOptions.headers["Content-Type"] = "application/json";
        console.log("API Request →", method, url, "(sending JSON)", body);
    } else {
        console.log("API Request →", method, url, "(no body)");
    }

    try {
        const response = await fetch(url, fetchOptions);
        console.log("Response status:", response.status);

        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            const json = await response.json();
            console.log("Response JSON:", json);
            return json;
        } else {
            const text = await response.text();
            console.log("Response Text:", text);
            return text;
        }
    } catch (error) {
        console.error("API Request Error:", error);
        throw error;
    }
}
