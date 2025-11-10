

// Утилиты для валидации
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

function showError(fieldId, message) {
    const field = document.getElementById(fieldId);
    const errorElement = document.getElementById(fieldId + 'Error');
    
    if (field) field.classList.add('error');
    if (errorElement) errorElement.textContent = message;
}

function clearError(fieldId) {
    const field = document.getElementById(fieldId);
    const errorElement = document.getElementById(fieldId + 'Error');
    
    if (field) field.classList.remove('error');
    if (errorElement) errorElement.textContent = '';
}

function setLoading(buttonId, isLoading) {
    const button = document.getElementById(buttonId);
    if (button) {
        if (isLoading) {
            button.classList.add('loading');
            button.disabled = true;
        } else {
            button.classList.remove('loading');
            button.disabled = false;
        }
    }
}

// Функции для форм авторизации
document.addEventListener('DOMContentLoaded', function() {
    // Обработка формы входа
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            
            // Очищаем предыдущие ошибки
            clearError('email');
            clearError('password');
            
            let hasErrors = false;
            
            // Валидация email
            if (!email) {
                showError('email', 'Введите email');
                hasErrors = true;
            } else if (!validateEmail(email)) {
                showError('email', 'Введите корректный email');
                hasErrors = true;
            }
            
            // Валидация пароля
            if (!password) {
                showError('password', 'Введите пароль');
                hasErrors = true;
            }
            
            if (hasErrors) return;
            
            setLoading('loginBtn', true);
            
            // Симуляция задержки
            setTimeout(() => {
                // Получаем список зарегистрированных пользователей
                const existingUsers = JSON.parse(localStorage.getItem('gigafood_users') || '[]');
                const user = existingUsers.find(u => u.email === email && u.password === password);
                
                setLoading('loginBtn', false);
                
                if (user) {
                    // Сохраняем данные текущего пользователя
                    localStorage.setItem('gigafood_current_user', JSON.stringify({
                        email: user.email,
                        loggedInAt: new Date().toISOString()
                    }));
                    
                    alert('Вход выполнен успешно!');
                    window.location.href = 'dashboard.html';
                } else {
                    showError('email', 'Неверный email или пароль');
                    showError('password', 'Проверьте правильность введенных данных');
                }
            }, 1000);
        });
    }
    
    // Обработка формы регистрации
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            // Очищаем предыдущие ошибки
            clearError('email');
            clearError('password');
            clearError('confirmPassword');
            
            let hasErrors = false;
            
            // Валидация email
            if (!email) {
                showError('email', 'Введите email');
                hasErrors = true;
            } else if (!validateEmail(email)) {
                showError('email', 'Введите корректный email');
                hasErrors = true;
            }
            
            // Валидация пароля
            if (!password) {
                showError('password', 'Введите пароль');
                hasErrors = true;
            } else if (password.length < 6) {
                showError('password', 'Пароль должен содержать минимум 6 символов');
                hasErrors = true;
            }
            
            // Валидация подтверждения пароля
            if (!confirmPassword) {
                showError('confirmPassword', 'Подтвердите пароль');
                hasErrors = true;
            } else if (password !== confirmPassword) {
                showError('confirmPassword', 'Пароли не совпадают');
                hasErrors = true;
            }
            
            if (hasErrors) return;
            
            setLoading('registerBtn', true);
            
            // Симуляция задержки
            setTimeout(() => {
                // Проверяем, не зарегистрирован ли уже такой email
                const existingUsers = JSON.parse(localStorage.getItem('gigafood_users') || '[]');
                const userExists = existingUsers.find(user => user.email === email);
                
                setLoading('registerBtn', false);
                
                if (userExists) {
                    showError('email', 'Пользователь с таким email уже зарегистрирован');
                    return;
                }
                
                // Сохраняем данные пользователя
                const userData = {
                    email: email,
                    password: password,
                    registeredAt: new Date().toISOString()
                };
                
                // Добавляем нового пользователя
                existingUsers.push(userData);
                localStorage.setItem('gigafood_users', JSON.stringify(existingUsers));
                
                alert('Регистрация выполнена успешно! Теперь вы можете войти в систему.');
                window.location.href = 'login.html';
            }, 1000);
        });
    }
    
    // Обработка формы сброса пароля
    const passwordResetForm = document.getElementById('passwordResetForm');
    if (passwordResetForm) {
        passwordResetForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value.trim();
            
            // Очищаем предыдущие ошибки
            clearError('email');
            
            // Валидация email
            if (!email) {
                showError('email', 'Введите email');
                return;
            } else if (!validateEmail(email)) {
                showError('email', 'Введите корректный email');
                return;
            }
            
            setLoading('resetBtn', true);
            
            // Симуляция задержки
            setTimeout(() => {
                setLoading('resetBtn', false);
                alert('Инструкции по сбросу пароля отправлены на вашу почту');
                window.location.href = 'login.html';
            }, 1000);
        });
    }
    
    // Обработка формы нового пароля
    const newPasswordForm = document.getElementById('newPasswordForm');
    if (newPasswordForm) {
        newPasswordForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            // Очищаем предыдущие ошибки
            clearError('newPassword');
            clearError('confirmPassword');
            
            let hasErrors = false;
            
            // Валидация нового пароля
            if (!newPassword) {
                showError('newPassword', 'Введите новый пароль');
                hasErrors = true;
            } else if (newPassword.length < 6) {
                showError('newPassword', 'Пароль должен содержать минимум 6 символов');
                hasErrors = true;
            }
            
            // Валидация подтверждения пароля
            if (!confirmPassword) {
                showError('confirmPassword', 'Подтвердите пароль');
                hasErrors = true;
            } else if (newPassword !== confirmPassword) {
                showError('confirmPassword', 'Пароли не совпадают');
                hasErrors = true;
            }
            
            if (hasErrors) return;
            
            setLoading('saveBtn', true);
            
            // Симуляция задержки
            setTimeout(() => {
                setLoading('saveBtn', false);
                alert('Пароль успешно изменен!');
                window.location.href = 'login.html';
            }, 1000);
        });
    }
    
    // Обработка формы профиля
    const profileForm = document.getElementById('profileForm');
    const profileInfo = document.getElementById('profileInfo');
    const editBtn = document.getElementById('editBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    
    if (profileForm && profileInfo && editBtn) {
        // Переключение между режимами просмотра и редактирования
        editBtn.addEventListener('click', function() {
            profileInfo.style.display = 'none';
            profileForm.style.display = 'block';
            editBtn.style.display = 'none';
        });
        
        if (cancelBtn) {
            cancelBtn.addEventListener('click', function() {
                profileForm.style.display = 'none';
                profileInfo.style.display = 'block';
                editBtn.style.display = 'block';
                updateProfileDisplay();
            });
        }
        
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
                updatedAt: new Date().toISOString()
            };
            
            localStorage.setItem('gigafood_user', JSON.stringify(profileData));
            
            // Обновляем отображение
            updateProfileDisplay();
            
            // Переключаемся в режим просмотра
            profileForm.style.display = 'none';
            profileInfo.style.display = 'block';
            editBtn.style.display = 'block';
            
            alert('Профиль сохранен!');
        });
        
        // Обработка переключателя авто-расчета
        const autoCalculateBtn = document.getElementById('autoCalculateBtn');
        const toggleSwitch = document.getElementById('toggleSwitch');
        const calorieLimitInput = document.getElementById('calorieLimit');
        
        if (autoCalculateBtn && toggleSwitch) {
            autoCalculateBtn.addEventListener('click', function() {
                toggleSwitch.classList.toggle('active');
                
                if (toggleSwitch.classList.contains('active')) {
                    // Автоматический расчет калорий
                    const age = parseInt(document.getElementById('age').value) || 30;
                    const weight = parseInt(document.getElementById('weight').value) || 75;
                    const height = parseInt(document.getElementById('height').value) || 180;
                    const gender = document.getElementById('gender').value;
                    const activity = document.getElementById('activity').value;
                    
                    // Простая формула расчета BMR
                    let bmr;
                    if (gender === 'male') {
                        bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
                    } else {
                        bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
                    }
                    
                    // Коэффициенты активности
                    const activityMultipliers = {
                        'low': 1.2,
                        'medium': 1.55,
                        'high': 1.9
                    };
                    
                    const calories = Math.round(bmr * activityMultipliers[activity]);
                    calorieLimitInput.value = calories;
                }
            });
        }
        
        // Функция обновления отображения профиля
        function updateProfileDisplay() {
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
        
        // Инициализация отображения профиля
        updateProfileDisplay();
    }
    
    // Обработка кнопок действий на дашборде
    const cameraBtn = document.querySelector('.btn-camera');
    if (cameraBtn) {
        cameraBtn.addEventListener('click', function() {
            alert('Функция фотографирования еды будет доступна в следующих версиях');
        });
    }
    
    const manualBtn = document.querySelector('.btn-manual');
    if (manualBtn) {
        manualBtn.addEventListener('click', function() {
            alert('Функция ручного ввода будет доступна в следующих версиях');
        });
    }
    
    // Анимация прогресс-бара
    const progressFill = document.querySelector('.progress-fill');
    if (progressFill) {
        setTimeout(() => {
            progressFill.style.width = '60%';
        }, 500);
    }
    
    // Обработка навигации
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const href = this.getAttribute('href');
            if (href && href !== '#') {
                window.location.href = href;
            }
        });
    });
});

// Функция удаления аккаунта
function deleteAccount() {
    if (confirm('Вы уверены, что хотите удалить аккаунт? Это действие нельзя отменить.')) {
        if (confirm('Это действие необратимо. Нажмите OK для подтверждения удаления.')) {
            alert('Аккаунт будет удален. Перенаправление на главную страницу...');
            window.location.href = 'index.html';
        }
    }
}

// Функции для работы с localStorage (сохранение данных пользователя)
function saveUserData(data) {
    localStorage.setItem('gigafood_user', JSON.stringify(data));
}

function getUserData() {
    const data = localStorage.getItem('gigafood_user');
    return data ? JSON.parse(data) : null;
}

// Функция для проверки авторизации
function checkAuth() {
    const currentUser = localStorage.getItem('gigafood_current_user');
    const isAuthPage = window.location.pathname.includes('login') || 
                      window.location.pathname.includes('register') || 
                      window.location.pathname.includes('index') ||
                      window.location.pathname.includes('password-reset') ||
                      window.location.pathname.includes('new-password');
    
    if (!currentUser && !isAuthPage) {
        window.location.href = 'login.html';
    } else if (currentUser && isAuthPage) {
        // Если пользователь уже авторизован, перенаправляем на дашборд
        window.location.href = 'dashboard.html';
    }
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    checkAuth();
    
    // Загрузка данных пользователя в профиль
    const userData = getUserData();
    if (userData && window.location.pathname.includes('profile')) {
        if (userData.fullName) document.getElementById('fullName').value = userData.fullName;
        if (userData.age) document.getElementById('age').value = userData.age;
        if (userData.height) document.getElementById('height').value = userData.height;
        if (userData.weight) document.getElementById('weight').value = userData.weight;
        if (userData.gender) document.getElementById('gender').value = userData.gender;
        if (userData.activity) document.getElementById('activity').value = userData.activity;
        if (userData.goal) document.getElementById('goal').value = userData.goal;
        if (userData.calorieLimit) document.getElementById('calorieLimit').value = userData.calorieLimit;
    }
});

// Функция для выхода из системы
function logout() {
    localStorage.removeItem('gigafood_current_user');
    localStorage.removeItem('gigafood_user');
    window.location.href = 'index.html';
}
