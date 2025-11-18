import { saveUserData, getUserData, getTokens } from './storage.js';
import { UserService } from './api/services/userS.js'

export async function initProfileForm() {
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
    
    profileForm.addEventListener('submit', async function(e) {
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
            gender: document.getElementById('gender').value.toUpperCase(),
            age: age,
            height: height,
            weight: weight,
            activityLevel: document.getElementById('activity').value.toUpperCase(),
            goalType: document.getElementById('goal').value.toUpperCase(),
            dailyCalorieLimit: parseInt(document.getElementById('calorieLimit').value),
            autoCalcCalloriesLimit: document.getElementById('autoCalculateBtn').dataset.active === 'true'
        };
        
        const response = await UserService.redactUser(
            profileData,
            { Authorization: getTokens()?.access }
        );
        
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
async function loadUserDataToForm() {
    if (true) {

        const responce = await UserService.getUserData( {Authorization: getTokens()?.access} );

        document.getElementById('fullName').value = '';
        document.getElementById('age').value = responce.user.age;
        document.getElementById('gender').value = responce.user.gender;
        document.getElementById('height').value = responce.user.height;
        document.getElementById('weight').value = responce.user.weight;
        document.getElementById('activity').value = responce.user.activityLevel;
        document.getElementById('goal').value = responce.user.goalType;
        document.getElementById('calorieLimit').value = responce.user.dailyCalorieLimit;
        
        // Восстанавливаем состояние авторасчета
        updateAutoCalculateState(responce.user.autoCalcCalloriesLimit !== false);
        
        // Показываем детали расчета если авторасчет включен
        if (responce.user.autoCalcCalloriesLimit !== false) {
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
async function calculateCalories() {
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
            'keep_fit': 1.0,
            'increase_str': 1.15
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

        const responce = await UserService.getUserData( {Authorization: getTokens()?.access} );

        
        calorieLimitInput.value = responce?.dailyCalorieLimit;
        
        // Показываем детали расчета сразу
        showCalculationDetails(bmr, tdee, calories, responce?.goalType);
    }
}

// Показ деталей расчета
function showCalculationDetails(bmr, tdee, finalCalories, goal) {
    const detailsContainer = document.getElementById('calculationDetails');
    if (!detailsContainer) return;
    
    const goalTexts = {
        'lose': 'похудения',
        'keep_fit': 'поддержания веса',
        'increase_str': 'набора массы'
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
export async function updateProfileDisplay() {
    const responce = await UserService.getUserData( {Authorization: getTokens()?.access} );

    if (true) {

        document.querySelector('.profile-name').textContent = 'Name State';
        document.getElementById('displayAge').textContent = responce.user.age;
        document.getElementById('displayGender').textContent = responce.user.gender === 'female' ? 'Женский' : 'Мужской';
        document.getElementById('displayHeight').textContent = (responce.user.height) + ' см';
        document.getElementById('displayWeight').textContent = (responce.user.weight) + ' кг';
        
        const activityLabels = {
            'lazy': 'Низкий',
            'normal': 'Средний',
            'sport': 'Высокий'
        };
        document.getElementById('displayActivity').textContent = activityLabels[responce.user.activity] || 'Средний';
        
        const goalLabels = {
            'lose': 'Похудение',
            'keep_fit': 'Поддержание веса',
            'increase_str': 'Набор веса'
        };
        document.getElementById('displayGoal').textContent = goalLabels[responce.user.goalType] || 'Поддержание веса';
        document.getElementById('displayCalories').textContent = (responce.user.dailyCalorieLimit || 2000) + ' ккал';
    }
}