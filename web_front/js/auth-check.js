import { getCurrentUser, logoutUser, getUserData } from './storage.js';

export function checkAuth() {
    const currentUser = getCurrentUser();
    const path = window.location.pathname;

    const onAuthPage =
        path.includes('login') ||
        path.includes('register') ||
        path.includes('index') ||
        path.includes('password-reset') ||
        path.includes('new-password');

    if (!currentUser && !onAuthPage) {
        window.location.href = 'login.html';
    }

    if (currentUser && onAuthPage) {
        window.location.href = 'dashboard.html';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();

    const logoutBtn = document.querySelector('.btn-logout');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            logoutUser();
            window.location.href = 'index.html';
        });
    }
});
