import { validateEmail, showError, clearError, setLoading } from './validation.js';
import { getUsers, saveUsers, getCurrentUser, saveCurrentUser } from './storage.js';

// Функции для форм авторизации
export function initLoginForm() {
    const loginForm = document.getElementById('loginForm');
    if (!loginForm) return;

    // Добавляем обработчики для валидации при потере фокуса
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');

    if (emailInput) {
        // Очистка ошибки при вводе
        emailInput.addEventListener('input', function() {
            clearError('email');
        });

        // Валидация при потере фокуса
        emailInput.addEventListener('blur', function() {
            const email = this.value.trim();
            if (email && !validateEmail(email)) {
                showError('email', 'Введите корректный email');
            }
        });
    }

    if (passwordInput) {
        // Очистка ошибки при вводе
        passwordInput.addEventListener('input', function() {
            clearError('password');
        });

        // Валидация при потере фокуса
        passwordInput.addEventListener('blur', function() {
            const password = this.value;
            if (!password) {
                showError('password', 'Введите пароль');
            }
        });
    }

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
            const existingUsers = getUsers();
            const user = existingUsers.find(u => u.email === email && u.password === password);
            
            setLoading('loginBtn', false);
            
            if (user) {
                // Сохраняем данные текущего пользователя
                saveCurrentUser({
                    email: user.email,
                    loggedInAt: new Date().toISOString()
                });
                
                alert('Вход выполнен успешно!');
                window.location.href = 'dashboard.html';
            } else {
                showError('email', 'Неверный email или пароль');
                showError('password', 'Проверьте правильность введенных данных');
            }
        }, 1000);
    });
}

export function initRegisterForm() {
    const registerForm = document.getElementById('registerForm');
    if (!registerForm) return;

    // Добавляем обработчики для валидации при потере фокуса
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    if (emailInput) {
        // Очистка ошибки при вводе
        emailInput.addEventListener('input', function() {
            clearError('email');
        });

        // Валидация при потере фокуса
        emailInput.addEventListener('blur', function() {
            const email = this.value.trim();
            if (email && !validateEmail(email)) {
                showError('email', 'Введите корректный email');
            }
        });
    }

    if (passwordInput) {
        // Очистка ошибки при вводе
        passwordInput.addEventListener('input', function() {
            clearError('password');
            // Также очищаем ошибку подтверждения пароля при изменении основного пароля
            clearError('confirmPassword');
            
            // Проверяем подтверждение пароля, если оно уже введено
            const confirmPassword = document.getElementById('confirmPassword').value;
            if (confirmPassword) {
                const password = this.value;
                if (password !== confirmPassword) {
                    showError('confirmPassword', 'Пароли не совпадают');
                } else {
                    clearError('confirmPassword');
                }
            }
        });

        // Валидация при потере фокуса
        passwordInput.addEventListener('blur', function() {
            const password = this.value;
            if (password && password.length < 6) {
                showError('password', 'Пароль должен содержать минимум 6 символов');
            }
        });
    }

    if (confirmPasswordInput) {
        // Очистка ошибки при вводе
        confirmPasswordInput.addEventListener('input', function() {
            clearError('confirmPassword');
        });

        // Валидация при потере фокуса
        confirmPasswordInput.addEventListener('blur', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            
            if (confirmPassword && password !== confirmPassword) {
                showError('confirmPassword', 'Пароли не совпадают');
            }
        });
    }

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
        
        // Если есть ошибки - прерываем отправку
        if (hasErrors) return;
        
        setLoading('registerBtn', true);
        
        // Симуляция задержки
        setTimeout(() => {
            const existingUsers = getUsers();
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
            saveUsers(existingUsers);
            
            alert('Регистрация выполнена успешно! Теперь вы можете войти в систему.');
            window.location.href = 'login.html';
        }, 1000);
    });
}

export function initPasswordResetForm() {
    const passwordResetForm = document.getElementById('passwordResetForm');
    if (!passwordResetForm) return;

    // Очистка ошибки при вводе email
    const emailInput = document.getElementById('email');
    if (emailInput) {
        // Очистка ошибки при вводе
        emailInput.addEventListener('input', function() {
            clearError('email');
        });

        // Валидация при потере фокуса
        emailInput.addEventListener('blur', function() {
            const email = this.value.trim();
            if (email && !validateEmail(email)) {
                showError('email', 'Введите корректный email');
            }
        });
    }

    passwordResetForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const email = document.getElementById('email').value.trim();
        
        // Очищаем предыдущие ошибки
        clearError('email');
        
        let hasErrors = false;
        
        // Валидация email
        if (!email) {
            showError('email', 'Введите email');
            hasErrors = true;
        } else if (!validateEmail(email)) {
            showError('email', 'Введите корректный email');
            hasErrors = true;
        }
        
        if (hasErrors) return;
        
        setLoading('resetBtn', true);
        
        // Симуляция задержки
        setTimeout(() => {
            setLoading('resetBtn', false);
            alert('Инструкции по сбросу пароля отправлены на вашу почту');
            window.location.href = 'login.html';
        }, 1000);
    });
}

export function initNewPasswordForm() {
    const newPasswordForm = document.getElementById('newPasswordForm');
    if (!newPasswordForm) return;

    // Добавляем обработчики для валидации при потере фокуса
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    if (newPasswordInput) {
        // Очистка ошибки при вводе
        newPasswordInput.addEventListener('input', function() {
            clearError('newPassword');
            // Также очищаем ошибку подтверждения пароля при изменении основного пароля
            clearError('confirmPassword');
            
            // Проверяем подтверждение пароля, если оно уже введено
            const confirmPassword = document.getElementById('confirmPassword').value;
            if (confirmPassword) {
                const newPassword = this.value;
                if (newPassword !== confirmPassword) {
                    showError('confirmPassword', 'Пароли не совпадают');
                } else {
                    clearError('confirmPassword');
                }
            }
        });

        // Валидация при потере фокуса
        newPasswordInput.addEventListener('blur', function() {
            const newPassword = this.value;
            if (newPassword && newPassword.length < 6) {
                showError('newPassword', 'Пароль должен содержать минимум 6 символов');
            }
        });
    }

    if (confirmPasswordInput) {
        // Очистка ошибки при вводе
        confirmPasswordInput.addEventListener('input', function() {
            clearError('confirmPassword');
        });

        // Валидация при потере фокуса
        confirmPasswordInput.addEventListener('blur', function() {
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = this.value;
            
            if (confirmPassword && newPassword !== confirmPassword) {
                showError('confirmPassword', 'Пароли не совпадают');
            }
        });
    }

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