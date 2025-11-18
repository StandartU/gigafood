import { ReportService } from './api/services/reportS.js'
import { getTokens } from './storage.js';
import { UserService } from './api/services/userS.js'

document.addEventListener('DOMContentLoaded', async () => {
    await loadWeekReport();
});

async function loadWeekReport() {
    try {
        const headers = { Authorization: getTokens()?.access };
        const response = await ReportService.weekReport(headers);
        const responceUser = await UserService.getUserData(headers);

        if (!response || !response.report) {
            console.warn('Нет данных для отчета');
            return;
        }

        const report = response.report;
        const dayCalories = response.dayCalories;

        // Обновляем статистику
        const statsGrid = document.querySelector('.stats-grid');

        const totalCalories = report.totalCalories || 0;
        const dailyLimit = responceUser.user.dailyCalorieLimit || 1; // защита от деления на 0
        const completionPercent = Math.round((totalCalories / (dailyLimit * 7)) * 100);

        if (statsGrid) {
            statsGrid.innerHTML = `
                <div class="stat-item">
                    <span class="stat-value">${totalCalories}</span>
                    <span class="stat-label">ккал потреблено</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">${dailyLimit * 7}</span>
                    <span class="stat-label">ккал цель</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">${completionPercent} %</span>
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
