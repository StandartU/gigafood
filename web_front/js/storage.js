// Функции для работы с localStorage
export function saveUserData(data) {
    localStorage.setItem('gigafood_user', JSON.stringify(data));
}

export function getUserData() {
    const data = localStorage.getItem('gigafood_user');
    return data ? JSON.parse(data) : null;
}

export function getUsers() {
    return JSON.parse(localStorage.getItem('gigafood_users') || '[]');
}

export function saveUsers(users) {
    localStorage.setItem('gigafood_users', JSON.stringify(users));
}

export function getCurrentUser() {
    const data = localStorage.getItem('gigafood_current_user');
    return data ? JSON.parse(data) : null;
}

export function saveCurrentUser(user) {
    localStorage.setItem('gigafood_current_user', JSON.stringify(user));
}

export function removeCurrentUser() {
    localStorage.removeItem('gigafood_current_user');
}

export function removeUserData() {
    localStorage.removeItem('gigafood_user');
}