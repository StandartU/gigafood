import { removeCurrentUser, removeUserData } from './storage.js';

export function initUIHandlers() {

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
}

// Функция удаления аккаунта
export function deleteAccount() {
    if (confirm('Вы уверены, что хотите удалить аккаунт? Это действие нельзя отменить.')) {
        if (confirm('Это действие необратимо. Нажмите OK для подтверждения удаления.')) {
            removeCurrentUser();
            removeUserData();
            alert('Аккаунт будет удален. Перенаправление на главную страницу...');
            window.location.href = 'index.html';
        }
    }
}

// Функция для выхода из системы
export function logout() {
    removeCurrentUser();
    removeUserData();
    window.location.href = 'index.html';
}