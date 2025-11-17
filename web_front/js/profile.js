import { saveUserData, getUserData } from './storage.js';

export function initProfileForm() {
    const profileForm = document.getElementById('profileForm');
    const profileInfo = document.getElementById('profileInfo');
    const editBtn = document.getElementById('editBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    
    if (!profileForm || !profileInfo || !editBtn) return;

    // Переключение между режимами просмотра и редактирования
    editBtn.addEventListener('click', function() {
        profileInfo.style.display = 'none';
        profileForm.style.display = 'block';
        editBtn.style.display = 'none';
        
        // Загружаем данные пользователя в форму
        loadUserDataToForm();
    });
    
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            profileForm.style.display = 'none';
            profileInfo.style.display = 'block';
            editBtn.style.display = 'block';
            updateProfileDisplay();
        });
    }
    
    // Автоматический расчет при изменении параметров
    setupAutoCalculation();
    
    profileForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const fullName = document.getElementById('fullName').value.trim();
        const age = parseInt(document.getElementById('age').value);
        const height = parseInt(document.getElementById('height').value);
        const weight = parseFloat(document.getElementById('weight').value);
        
        // Валидация данных
        if (!fullName) {
            alert('Введите имя');
            return;
        }
        
        if (age < 16 || age > 100) {
            alert('Возраст должен быть от 16 до 100 лет');
            return;
        }
        
        if (height < 120 || height > 250) {
            alert('Рост должен быть от 120 до 250 см');
            return;
        }
        
        if (weight < 30 || weight > 300) {
            alert('Вес должен быть от 30 до 300 кг');
            return;
        }
        
        // Сохраняем данные профиля
        const profileData = {
            fullName: fullName,
            age: age,
            gender: document.getElementById('gender').value,
            height: height,
            weight: weight,
            activity: document.getElementById('activity').value,
            goal: document.getElementById('goal').value,
            calorieLimit: parseInt(document.getElementById('calorieLimit').value),
            autoCalculate: document.getElementById('autoCalculateBtn').dataset.active === 'true',
            updatedAt: new Date().toISOString()
        };
        
        saveUserData(profileData);
        
        // Обновляем отображение
        updateProfileDisplay();
        
        // Переключаемся в режим просмотра
        profileForm.style.display = 'none';
        profileInfo.style.display = 'block';
        editBtn.style.display = 'block';
        
        alert('Профиль сохранен!');
    });
    
    initAutoCalculate();
    updateProfileDisplay();
}

// Загрузка данных пользователя в форму
function loadUserDataToForm() {
    const userData = getUserData();
    if (userData) {
        document.getElementById('fullName').value = userData.fullName || '';
        document.getElementById('age').value = userData.age || 30;
        document.getElementById('gender').value = userData.gender || 'male';
        document.getElementById('height').value = userData.height || 180;
        document.getElementById('weight').value = userData.weight || 75;
        document.getElementById('activity').value = userData.activity || 'medium';
        document.getElementById('goal').value = userData.goal || 'maintain';
        document.getElementById('calorieLimit').value = userData.calorieLimit || 2000;
        
        // Восстанавливаем состояние авторасчета
        updateAutoCalculateState(userData.autoCalculate !== false);
        
        // Показываем детали расчета если авторасчет включен
        if (userData.autoCalculate !== false) {
            calculateCalories();
        }
    }
}

// Обновление состояния авторасчета (упрощенная версия без свитча)
function updateAutoCalculateState(isActive) {
    const autoCalculateBtn = document.getElementById('autoCalculateBtn');
    const calorieLimitInput = document.getElementById('calorieLimit');
    
    if (!autoCalculateBtn) return;
    
    autoCalculateBtn.dataset.active = isActive;
    
    if (isActive) {
        autoCalculateBtn.textContent = 'Авторасчет включен ✓';
        autoCalculateBtn.style.background = '#4CAF50';
        calorieLimitInput.disabled = true;
        calorieLimitInput.style.background = '#f8f9fa';
    } else {
        autoCalculateBtn.textContent = 'Включить авторасчет';
        autoCalculateBtn.style.background = '#2196F3';
        calorieLimitInput.disabled = false;
        calorieLimitInput.style.background = '#fff';
    }
}

// Настройка автоматического расчета при изменении параметров
function setupAutoCalculation() {
    const inputs = ['age', 'height', 'weight', 'gender', 'activity', 'goal'];
    
    inputs.forEach(inputId => {
        const input = document.getElementById(inputId);
        if (input) {
            input.addEventListener('change', function() {
                const autoCalculateBtn = document.getElementById('autoCalculateBtn');
                if (autoCalculateBtn && autoCalculateBtn.dataset.active === 'true') {
                    calculateCalories();
                }
            });
        }
    });
}

// Улучшенная функция расчета калорий
function calculateCalories() {
    const autoCalculateBtn = document.getElementById('autoCalculateBtn');
    const calorieLimitInput = document.getElementById('calorieLimit');
    
    if (!autoCalculateBtn || !calorieLimitInput) return;
    
    if (autoCalculateBtn.dataset.active === 'true') {
        const age = parseInt(document.getElementById('age').value) || 30;
        const weight = parseFloat(document.getElementById('weight').value) || 75;
        const height = parseInt(document.getElementById('height').value) || 180;
        const gender = document.getElementById('gender').value || 'male';
        const activity = document.getElementById('activity').value || 'medium';
        const goal = document.getElementById('goal').value || 'maintain';
        
        // Расчет BMR (Basal Metabolic Rate) по формуле Миффлина-Сан Жеора
        let bmr;
        if (gender === 'male') {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        
        // Коэффициенты активности
        const activityMultipliers = {
            'low': 1.2,
            'medium': 1.55,
            'high': 1.9
        };
        
        // Расчет TDEE (Total Daily Energy Expenditure)
        const tdee = Math.round(bmr * activityMultipliers[activity]);
        
        // Корректировка по цели
        const goalMultipliers = {
            'lose': 0.85,
            'maintain': 1.0,
            'gain': 1.15
        };
        
        let calories = Math.round(tdee * goalMultipliers[goal]);
        
        // Дополнительная корректировка для экстремальных целей
        if (goal === 'lose') {
            const minCalories = gender === 'male' ? 1500 : 1200;
            calories = Math.max(calories, minCalories);
        } else if (goal === 'gain') {
            const maxCalories = tdee + 500;
            calories = Math.min(calories, maxCalories);
        }
        
        calorieLimitInput.value = calories;
        
        // Показываем детали расчета сразу
        showCalculationDetails(bmr, tdee, calories, goal);
    }
}

// Показ деталей расчета
function showCalculationDetails(bmr, tdee, finalCalories, goal) {
    const detailsContainer = document.getElementById('calculationDetails');
    if (!detailsContainer) return;
    
    const goalTexts = {
        'lose': 'похудения',
        'maintain': 'поддержания веса',
        'gain': 'набора массы'
    };
    
    detailsContainer.innerHTML = `
        <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin-top: 10px; font-size: 14px;">
            <strong>Детали расчета:</strong>
            <div>BMR (основной обмен): ${Math.round(bmr)} ккал/день</div>
            <div>TDEE (общий расход): ${tdee} ккал/день</div>
        </div>
    `;
}

// Инициализация кнопки авторасчета (упрощенная версия)
function initAutoCalculate() {
    const autoCalculateBtn = document.getElementById('autoCalculateBtn');
    const calorieLimitInput = document.getElementById('calorieLimit');
    
    if (!autoCalculateBtn) return;

    // Устанавливаем начальное состояние
    if (!autoCalculateBtn.dataset.active) {
        autoCalculateBtn.dataset.active = 'true';
        updateAutoCalculateState(true);
    }

    autoCalculateBtn.addEventListener('click', function(e) {
        e.preventDefault();
        
        const isCurrentlyActive = autoCalculateBtn.dataset.active === 'true';
        const newState = !isCurrentlyActive;
        
        updateAutoCalculateState(newState);
        
        if (newState) {
            // Включили авторасчет - пересчитываем калории
            calculateCalories();
        } else {
            // Выключили авторасчет - очищаем детали расчета
            const detailsContainer = document.getElementById('calculationDetails');
            if (detailsContainer) {
                detailsContainer.innerHTML = '';
            }
        }
    });
    
    // Создаем контейнер для деталей расчета, если его нет
    if (!document.getElementById('calculationDetails')) {
        const detailsContainer = document.createElement('div');
        detailsContainer.id = 'calculationDetails';
        detailsContainer.style.marginTop = '10px';
        detailsContainer.style.marginBottom = '0';
        detailsContainer.style.border = 'none';
        
        const calorieLimitGroup = document.getElementById('calorieLimit').closest('.form-group');
        if (calorieLimitGroup) {
            calorieLimitGroup.appendChild(detailsContainer);
        }
    }
}

// Функция обновления отображения профиля
export function updateProfileDisplay() {
    const userData = getUserData();
    if (userData) {
        document.querySelector('.profile-name').textContent = userData.fullName || 'Иван Иванов';
        document.getElementById('displayAge').textContent = (userData.age || 30) + ' лет';
        document.getElementById('displayGender').textContent = userData.gender === 'female' ? 'Женский' : 'Мужской';
        document.getElementById('displayHeight').textContent = (userData.height || 180) + ' см';
        document.getElementById('displayWeight').textContent = (userData.weight || 75) + ' кг';
        
        const activityLabels = {
            'low': 'Низкий',
            'medium': 'Средний',
            'high': 'Высокий'
        };
        document.getElementById('displayActivity').textContent = activityLabels[userData.activity] || 'Средний';
        
        const goalLabels = {
            'lose': 'Похудение',
            'maintain': 'Поддержание веса',
            'gain': 'Набор веса'
        };
        document.getElementById('displayGoal').textContent = goalLabels[userData.goal] || 'Поддержание веса';
        document.getElementById('displayCalories').textContent = (userData.calorieLimit || 2000) + ' ккал';
    }
}