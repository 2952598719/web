
export function getToken() {
    return localStorage.getItem('token')
}

export function setToken(token: string) {
    localStorage.setItem('token', token)
}

export function removeToken() {
    localStorage.removeItem('token');
}

export function getBackground() {
    return localStorage.getItem('backgroundPreference')
}

export function setBackground(background: string) {
    localStorage.setItem('backgroundPreference', background)
}
