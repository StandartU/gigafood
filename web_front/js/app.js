import { initLoginForm, initRegisterForm, initPasswordResetForm, initNewPasswordForm } from './auth.js';
import { initProfileForm, updateProfileDisplay } from './profile.js';
import { initUIHandlers, deleteAccount, logout } from './ui.js';
import { getCurrentUser, getUserData } from './storage.js';

// Функция для проверки авторизации
function checkAuth() {
    const currentUser = getCurrentUser();
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
    
    // Инициализация форм
    initLoginForm();
    initRegisterForm();
    initPasswordResetForm();
    initNewPasswordForm();
    initProfileForm();
    initUIHandlers();
    
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

// Экспортируем функции для глобального использования
window.deleteAccount = deleteAccount;
window.logout = logout;