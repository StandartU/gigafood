import { ReportService } from './api/services/reportS.js'
import { getTokens } from './storage.js';

document.addEventListener('DOMContentLoaded', async () => {
    await loadWeekReport();
});

async function loadWeekReport() {
    try {
        const headers = { Authorization: getTokens()?.access };
        const response = await ReportService.weekReport(headers);

        if (!response || !response.report) {
            console.warn('Нет данных для отчета');
            return;
        }

        const report = response.report;
        const dayCalories = response.dayCalories;

        // Обновляем статистику
        const statsGrid = document.querySelector('.stats-grid');
        if (statsGrid) {
            statsGrid.innerHTML = `
                <div class="stat-item">
                    <span class="stat-value">${report.totalCalories}</span>
                    <span class="stat-label">ккал потреблено</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">---</span>
                    <span class="stat-label">ккал цель</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">---</span>
                    <span class="stat-label">выполнение цели</span>
                </div>
            `;
        }

        // Обновляем средние показатели
        const averagesDiv = document.querySelector('.averages');
        if (averagesDiv && dayCalories) {
            // Считаем среднее за неделю
            const totalDays = Object.keys(dayCalories).length;
            const totalProteins = 0; // сюда можно добавить реальные данные
            const totalFats = 0;
            const totalCarbs = 0;

            averagesDiv.innerHTML = `
                <div class="average-item">
                    <span class="average-label">Белки:</span>
                    <span class="average-value">${totalProteins}г/день</span>
                </div>
                <div class="average-item">
                    <span class="average-label">Жиры:</span>
                    <span class="average-value">${totalFats}г/день</span>
                </div>
                <div class="average-item">
                    <span class="average-label">Углеводы:</span>
                    <span class="average-value">${totalCarbs}г/день</span>
                </div>
            `;
        }

        // Показываем рекомендации
        const recomendationsDiv = document.querySelector('.recomendations');
        if (recomendationsDiv && report.recomendations) {
            recomendationsDiv.textContent = report.recomendations;
        }

    } catch (err) {
        console.error('Ошибка при загрузке недельного отчета:', err);
    }
}
