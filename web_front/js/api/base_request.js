const BASE_URL = "https://localhost:8080/gigafood/api/v1";

export async function apiRequest({ path, method = "GET", body = null, headers = {} }) {
    const url = `${BASE_URL}${path}`;
    
    const fetchOptions = {
        method,
        headers: {
            "Content-Type": "application/json",
            ...headers,
        },
    };

    if (body) {
        fetchOptions.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(url, fetchOptions);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // проверяем есть ли контент
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return await response.json();
        } else {
            return await response.text();
        }
    } catch (error) {
        console.error("API Request Error:", error);
        throw error;
    }
}
