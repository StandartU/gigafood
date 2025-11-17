import { getUserData, saveUserData } from './storage.js';

// Данные за неделю
function getWeeklyData() {
    const days = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'];
    const userData = getUserData();
    const goal = userData ? userData.calorieLimit : 2000;

    return {
        labels: days,
        datasets: days.map((day, index) => {
            const base = goal - 300;
            const variation = Math.floor(Math.random() * 600);
            const consumed = Math.max(500, Math.min(goal + 200, base + variation));
            return { day, consumed, goal };
        })
    };
}

function recognizeFood() {
    return new Promise(resolve => {
        setTimeout(() => {
            const foods = [
                {
                    name: 'Омлет с овощами',
                    calories: 320,
                    protein: 18,
                    fat: 22,
                    carbs: 12
                },
                {
                    name: 'Куриная грудка с рисом',
                    calories: 480,
                    protein: 45,
                    fat: 8,
                    carbs: 55
                },
                {
                    name: 'Салат Цезарь',
                    calories: 410,
                    protein: 15,
                    fat: 28,
                    carbs: 22
                },
                {
                    name: 'Паста карбонара',
                    calories: 720,
                    protein: 24,
                    fat: 38,
                    carbs: 68
                },
                {
                    name: 'Смузи с бананом',
                    calories: 280,
                    protein: 6,
                    fat: 3,
                    carbs: 58
                },
                {
                    name: 'Бургер с картошкой',
                    calories: 850,
                    protein: 32,
                    fat: 42,
                    carbs: 88
                }
            ];
            resolve(foods[Math.floor(Math.random() * foods.length)]);
        }, 800);
    });
}

// Переменная для хранения текущего элемента еды
let currentFoodItem = null;

function addFoodToTape(food, imageUrl = null) {
    const tape = document.getElementById('foodTape');
    const card = document.getElementById('foodTapeCard');
    const container = card.querySelector('.food-tape-container');
    const empty = tape.querySelector('.empty-state');

    // Показываем карточку и убираем пустое состояние
    if (empty) {
        empty.remove();
    }
    
    card.style.display = 'block';
    requestAnimationFrame(() => container.classList.add('visible'));

    const item = document.createElement('div');
    item.className = 'food-item';
    item.dataset.food = JSON.stringify(food);
    if (imageUrl) {
        item.dataset.image = imageUrl;
    }
    
    // Генерируем уникальный ID для элемента
    item.dataset.id = 'food_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);

    // Если нет фото, показываем иконку вместо изображения
    if (!imageUrl) {
        item.innerHTML = `
            <div class="food-thumb no-image">
                <span class="food-icon">🍽️</span>
            </div>
            <div class="food-info">
                <p class="food-name">${food.name}</p>
                <p class="food-calories">${food.calories} ккал</p>
            </div>
        `;
    } else {
        item.innerHTML = `
            <img src="${imageUrl}" class="food-thumb" alt="${food.name}">
            <div class="food-info">
                <p class="food-name">${food.name}</p>
                <p class="food-calories">${food.calories} ккал</p>
            </div>
        `;
    }

    item.addEventListener('click', () => openFoodModal(food, imageUrl, item));
    tape.appendChild(item);
    tape.scrollLeft = tape.scrollWidth;
}

function openFoodModal(food, imageUrl = null, foodItem = null) {
    const modal = document.getElementById('foodModal');
    const title = document.getElementById('modalTitle');
    const image = document.getElementById('modalImage');
    const icon = document.getElementById('modalIcon');
    const imageContainer = document.getElementById('modalImageContainer');
    const calories = document.getElementById('modalCalories');
    const protein = document.getElementById('modalProtein');
    const fat = document.getElementById('modalFat');
    const carbs = document.getElementById('modalCarbs');

    // Сохраняем ссылку на текущий элемент
    currentFoodItem = foodItem;

    title.textContent = food.name;
    
    // Если есть фото, показываем его, иначе - иконку
    if (imageUrl) {
        image.src = imageUrl;
        image.style.display = 'block';
        icon.style.display = 'none';
    } else {
        image.style.display = 'none';
        icon.style.display = 'flex';
    }
    
    calories.textContent = food.calories;
    protein.textContent = food.protein || 0;
    fat.textContent = food.fat || 0;
    carbs.textContent = food.carbs || 0;

    document.body.classList.add('modal-open');
    modal.style.display = 'flex';
    
    requestAnimationFrame(() => {
        modal.classList.add('visible');
        modal.querySelector('.modal-content').classList.add('visible');
    });
}

function closeFoodModal() {
    const modal = document.getElementById('foodModal');
    const modalContent = modal.querySelector('.modal-content');
    
    modal.classList.remove('visible');
    modalContent.classList.remove('visible');
    
    setTimeout(() => {
        modal.style.display = 'none';
        document.body.classList.remove('modal-open');
        currentFoodItem = null;
    }, 300);
}

// Функция открытия модального окна редактирования
function openEditModal() {
    if (!currentFoodItem) return;
    
    const food = JSON.parse(currentFoodItem.dataset.food);
    const editModal = document.getElementById('editFoodModal');
    
    // Заполняем форму данными
    document.getElementById('editFoodId').value = currentFoodItem.dataset.id;
    document.getElementById('editFoodName').value = food.name;
    document.getElementById('editFoodCalories').value = food.calories;
    document.getElementById('editFoodProtein').value = food.protein || 0;
    document.getElementById('editFoodFat').value = food.fat || 0;
    document.getElementById('editFoodCarbs').value = food.carbs || 0;
    
    // Закрываем модальное окно просмотра
    closeFoodModal();
    
    // Открываем модальное окно редактирования
    document.body.classList.add('modal-open');
    editModal.style.display = 'flex';
    
    requestAnimationFrame(() => {
        editModal.classList.add('visible');
        editModal.querySelector('.modal-content').classList.add('visible');
    });
}

// Функция закрытия модального окна редактирования
window.closeEditModal = function() {
    const modal = document.getElementById('editFoodModal');
    const modalContent = modal.querySelector('.modal-content');
    
    modal.classList.remove('visible');
    modalContent.classList.remove('visible');
    
    setTimeout(() => {
        modal.style.display = 'none';
        document.body.classList.remove('modal-open');
    }, 300);
}

// Функция удаления текущего блюда
window.deleteCurrentFood = function() {
    if (!confirm('Вы уверены, что хотите удалить это блюдо?')) {
        return;
    }
    
    const foodId = document.getElementById('editFoodId').value;
    const foodItem = document.querySelector(`[data-id="${foodId}"]`);
    
    if (foodItem) {
        const food = JSON.parse(foodItem.dataset.food);
        
        // Обновляем счётчик калорий (вычитаем)
        updateConsumedCalories(-food.calories);
        
        // Удаляем элемент
        foodItem.remove();
        
        // Проверяем, остались ли элементы в ленте
        const tape = document.getElementById('foodTape');
        if (tape.children.length === 0) {
            tape.innerHTML = `
                <div class="empty-state">
                    <p>Пока ничего не добавлено</p>
                    <p>Загрузите фото еды, чтобы начать</p>
                </div>
            `;
            const card = document.getElementById('foodTapeCard');
            const container = card.querySelector('.food-tape-container');
            container.classList.remove('visible');
            setTimeout(() => {
                card.style.display = 'none';
            }, 300);
        }
    }
    
    closeEditModal();
}

// Обработка отправки формы редактирования
function handleEditFoodSubmit(e) {
    e.preventDefault();
    
    const foodId = document.getElementById('editFoodId').value;
    const foodItem = document.querySelector(`[data-id="${foodId}"]`);
    
    if (!foodItem) return;
    
    const oldFood = JSON.parse(foodItem.dataset.food);
    
    const name = document.getElementById('editFoodName').value.trim();
    const calories = parseInt(document.getElementById('editFoodCalories').value);
    const protein = parseInt(document.getElementById('editFoodProtein').value) || 0;
    const fat = parseInt(document.getElementById('editFoodFat').value) || 0;
    const carbs = parseInt(document.getElementById('editFoodCarbs').value) || 0;
    
    if (!name || isNaN(calories)) {
        alert('Пожалуйста, заполните название блюда и калории');
        return;
    }
    
    const updatedFood = {
        name,
        calories,
        protein,
        fat,
        carbs
    };
    
    // Обновляем данные в элементе
    foodItem.dataset.food = JSON.stringify(updatedFood);
    
    // Обновляем отображение в ленте
    const nameEl = foodItem.querySelector('.food-name');
    const caloriesEl = foodItem.querySelector('.food-calories');
    if (nameEl) nameEl.textContent = name;
    if (caloriesEl) caloriesEl.textContent = `${calories} ккал`;
    
    // Обновляем счётчик калорий (разница между старым и новым значением)
    const calorieDiff = calories - oldFood.calories;
    updateConsumedCalories(calorieDiff);
    
    closeEditModal();
}

// Функции для модального окна ручного ввода
function openManualInputModal() {
    const modal = document.getElementById('manualInputModal');
    const form = document.getElementById('manualFoodForm');
    
    // Сброс формы
    form.reset();
    
    document.body.classList.add('modal-open');
    modal.style.display = 'flex';
    
    requestAnimationFrame(() => {
        modal.classList.add('visible');
        modal.querySelector('.modal-content').classList.add('visible');
    });
}

window.closeManualInputModal = function() {
    const modal = document.getElementById('manualInputModal');
    const modalContent = modal.querySelector('.modal-content');
    
    modal.classList.remove('visible');
    modalContent.classList.remove('visible');
    
    setTimeout(() => {
        modal.style.display = 'none';
        document.body.classList.remove('modal-open');
    }, 300);
}

// Обработка отправки формы
function handleManualFoodSubmit(e) {
    e.preventDefault();
    
    const name = document.getElementById('foodName').value.trim();
    const calories = parseInt(document.getElementById('foodCalories').value);
    const protein = parseInt(document.getElementById('foodProtein').value) || 0;
    const fat = parseInt(document.getElementById('foodFat').value) || 0;
    const carbs = parseInt(document.getElementById('foodCarbs').value) || 0;
    
    if (!name || isNaN(calories)) {
        alert('Пожалуйста, заполните название блюда и калории');
        return;
    }
    
    const food = {
        name,
        calories,
        protein,
        fat,
        carbs
    };
    
    // Добавляем еду в ленту без фото
    addFoodToTape(food, null);
    updateConsumedCalories(food.calories);
    closeManualInputModal();
}

// Обновление потреблённых калорий
function updateConsumedCalories(additional) {
    const consumedEl = document.getElementById('calorieConsumed');
    const progressFill = document.getElementById('progressFill');
    const userData = getUserData();
    const goal = userData?.calorieLimit || 2000;

    let currentText = consumedEl.textContent;
    let currentConsumed = parseInt(currentText.match(/\d+/)?.[0] || '0', 10);
    const newConsumed = Math.max(0, currentConsumed + additional);

    consumedEl.textContent = `Потреблено: ${newConsumed} ккал`;

    const percentage = Math.min((newConsumed / goal) * 100, 100);
    progressFill.style.width = `${percentage}%`;
}

// Инициализация графика
let chartInstance = null;
function initChart() {
    const ctx = document.getElementById('weeklyChart');
    if (!ctx) return;

    if (chartInstance) chartInstance.destroy();

    const weeklyData = getWeeklyData();
    const todayIndex = new Date().getDay() === 0 ? 6 : new Date().getDay() - 1;

    chartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: weeklyData.labels,
            datasets: [{
                label: 'Потреблено ккал',
                data: weeklyData.datasets.map(d => d.consumed),
                borderColor: '#4CAF50',
                backgroundColor: 'rgba(76, 175, 80, 0.15)',
                borderWidth: 4,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#4CAF50',
                pointBorderColor: '#ffffff',
                pointBorderWidth: 3,
                pointRadius: 6,
                pointHoverRadius: 8,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: { duration: 1500, easing: 'easeOutQuart' },
            layout: { padding: { top: 20, right: 20, bottom: 10, left: 10 } },
            scales: {
                y: { display: false, beginAtZero: false, grid: { display: false } },
                x: {
                    grid: { display: false },
                    ticks: { color: '#666', font: { size: 13, weight: 'bold' }, padding: 8 },
                    border: { display: true, color: '#e0e0e0', width: 1, dash: [4, 4] }
                }
            },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleColor: '#fff',
                    bodyColor: '#fff',
                    borderColor: '#4CAF50',
                    borderWidth: 2,
                    cornerRadius: 8,
                    displayColors: false,
                    callbacks: {
                        title: (items) => items[0].label,
                        label: (ctx) => `Потреблено: ${ctx.parsed.y} ккал`,
                        afterLabel: (ctx) => {
                            const goal = weeklyData.datasets[ctx.dataIndex].goal;
                            const diff = ctx.parsed.y - goal;
                            if (diff > 0) return `Превышение: +${diff} ккал`;
                            if (diff < 0) return `Не достигнуто: ${diff} ккал`;
                            return 'Цель достигнута!';
                        }
                    }
                }
            },
            interaction: { intersect: false, mode: 'index' }
        }
    });

    // Подсветка сегодняшнего дня
    setTimeout(() => {
        const meta = chartInstance.getDatasetMeta(0);
        meta.data[todayIndex].pointBackgroundColor = '#ff9800';
        meta.data[todayIndex].pointBorderColor = '#fff';
        chartInstance.update('none');
    }, 100);
}

// Обновление дашборда
function updateDashboard() {
    const userData = getUserData();
    const welcomeElement = document.getElementById('welcomeUser');
    const calorieGoalElement = document.getElementById('calorieGoal');

    if (userData) {
        const name = userData.fullName || 'Пользователь';
        const firstName = name.split(' ')[0];
        welcomeElement.textContent = `Добро пожаловать, ${firstName}!`;

        const goal = userData.calorieLimit || 2000;
        calorieGoalElement.textContent = `${goal} ккал`;

        // Сегодняшние калории (из ленты или мок)
        const todayData = getWeeklyData().datasets[new Date().getDay() === 0 ? 6 : new Date().getDay() - 1];
        updateConsumedCalories(todayData.consumed - 800);
    }

    setTimeout(initChart, 300);
}

// Инициализация
document.addEventListener('DOMContentLoaded', function() {
    updateDashboard();

    // Скрытый input для загрузки фото
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/*';
    fileInput.style.display = 'none';
    document.body.appendChild(fileInput);

    // Кнопка камеры
    const cameraBtn = document.querySelector('.btn-camera');
    if (cameraBtn) {
        cameraBtn.addEventListener('click', () => fileInput.click());
    }

    // Кнопка ручного ввода
    const manualBtn = document.querySelector('.btn-manual');
    if (manualBtn) {
        manualBtn.addEventListener('click', openManualInputModal);
    }

    // Обработка отправки формы ручного ввода
    const manualForm = document.getElementById('manualFoodForm');
    if (manualForm) {
        manualForm.addEventListener('submit', handleManualFoodSubmit);
    }

    // Обработка отправки формы редактирования
    const editForm = document.getElementById('editFoodForm');
    if (editForm) {
        editForm.addEventListener('submit', handleEditFoodSubmit);
    }

    // Обработка загрузки фото
    fileInput.addEventListener('change', async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const imageUrl = URL.createObjectURL(file);
        const tape = document.getElementById('foodTape');

        const loadingItem = document.createElement('div');
        loadingItem.className = 'food-item loading';
        loadingItem.innerHTML = `
            <div class="food-thumb skeleton"></div>
            <div class="food-info">
                <p class="skeleton-text">Распознаём...</p>
                <p class="skeleton-text small"></p>
            </div>
        `;
        tape.appendChild(loadingItem);

        try {
            const food = await recognizeFood(file);
            loadingItem.remove();
            addFoodToTape(food, imageUrl);
            updateConsumedCalories(food.calories);
        } catch (err) {
            loadingItem.remove();
            alert('Не удалось распознать еду. Попробуйте ещё раз.');
        }

        fileInput.value = '';
    });

    // Закрытие модальных окон
    const foodModal = document.getElementById('foodModal');
    const foodModalContent = foodModal.querySelector('.modal-content');
    const foodCloseBtn = foodModal.querySelector('.modal-close');

    foodCloseBtn.addEventListener('click', closeFoodModal);
    foodModal.addEventListener('click', function(e) {
        if (e.target === foodModal) {
            closeFoodModal();
        }
    });
    foodModalContent.addEventListener('click', function(e) {
        e.stopPropagation();
    });

    // Добавляем обработчики для кнопок редактирования и удаления в модальном окне просмотра
    const editBtn = document.createElement('button');
    editBtn.className = 'btn btn-primary';
    editBtn.textContent = 'Редактировать';
    editBtn.style.marginRight = '8px';
    editBtn.addEventListener('click', openEditModal);

    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'btn btn-danger';
    deleteBtn.textContent = 'Удалить';
    deleteBtn.addEventListener('click', function() {
        if (!currentFoodItem) return;
        
        if (!confirm('Вы уверены, что хотите удалить это блюдо?')) {
            return;
        }
        
        const food = JSON.parse(currentFoodItem.dataset.food);
        updateConsumedCalories(-food.calories);
        currentFoodItem.remove();
        
        // Проверяем, остались ли элементы в ленте
        const tape = document.getElementById('foodTape');
        if (tape.children.length === 0) {
            tape.innerHTML = `
                <div class="empty-state">
                    <p>Пока ничего не добавлено</p>
                    <p>Загрузите фото еды, чтобы начать</p>
                </div>
            `;
            const card = document.getElementById('foodTapeCard');
            const container = card.querySelector('.food-tape-container');
            container.classList.remove('visible');
            setTimeout(() => {
                card.style.display = 'none';
            }, 300);
        }
        
        closeFoodModal();
    });

    const modalBody = foodModal.querySelector('.modal-body');
    const actionButtons = document.createElement('div');
    actionButtons.className = 'modal-actions';
    actionButtons.style.marginTop = '20px';
    actionButtons.style.display = 'flex';
    actionButtons.style.gap = '8px';
    actionButtons.style.justifyContent = 'center';
    actionButtons.appendChild(editBtn);
    actionButtons.appendChild(deleteBtn);
    modalBody.appendChild(actionButtons);

    // Закрытие модального окна ручного ввода
    const manualModal = document.getElementById('manualInputModal');
    const manualModalContent = manualModal.querySelector('.modal-content');

    manualModal.addEventListener('click', function(e) {
        if (e.target === manualModal) {
            closeManualInputModal();
        }
    });
    manualModalContent.addEventListener('click', function(e) {
        e.stopPropagation();
    });

    // Закрытие модального окна редактирования
    const editModal = document.getElementById('editFoodModal');
    const editModalContent = editModal.querySelector('.modal-content');

    editModal.addEventListener('click', function(e) {
        if (e.target === editModal) {
            closeEditModal();
        }
    });
    editModalContent.addEventListener('click', function(e) {
        e.stopPropagation();
    });

    // Закрытие по Escape для всех модальных окон
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            if (foodModal.style.display === 'flex') {
                closeFoodModal();
            }
            if (manualModal.style.display === 'flex') {
                closeManualInputModal();
            }
            if (editModal.style.display === 'flex') {
                closeEditModal();
            }
        }
    });
});