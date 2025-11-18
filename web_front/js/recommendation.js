import { ReportService } from "./api/services/reportS.js";
import { getTokens } from './storage.js';

document.addEventListener('DOMContentLoaded', async () => {
    await loadRecommendations();
});

export async function loadRecommendations() {
    try {
        const responce = await ReportService.weekReport({ Authorization: getTokens()?.access });
        const list = document.querySelector(".recommendations-list");
        if (!list) return;

        list.innerHTML = "";

        const li = document.createElement("li");
        li.textContent = responce.report.recomendations;;
        list.appendChild(li);

        responce.recomendations;
    } catch (err) {
        console.error("Ошибка загрузки рекомендаций:", err);
    }
}