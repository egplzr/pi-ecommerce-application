// Utilitário para manipulação de tokens CSRF para integração com Spring Security
document.addEventListener('DOMContentLoaded', function() {
    // Configurar interceptor para todas as requisições fetch
    const originalFetch = window.fetch;

    window.fetch = function(url, options = {}) {
        if (!options.headers) {
            options.headers = {};
        }

        // Adiciona o token CSRF para métodos não seguros (POST, PUT, DELETE, PATCH)
        if (options.method && ['POST', 'PUT', 'DELETE', 'PATCH'].includes(options.method.toUpperCase())) {
            // Obter o token CSRF da meta tag
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

            if (csrfToken && csrfHeader) {
                options.headers[csrfHeader] = csrfToken;
            }
        }

        // Adicionar Content-Type como application/json se não estiver definido e o corpo for um objeto
        if (options.body && typeof options.body === 'object' && !(options.body instanceof FormData)) {
            if (!options.headers['Content-Type']) {
                options.headers['Content-Type'] = 'application/json';
            }

            // Converter body para JSON se for um objeto
            if (options.headers['Content-Type'] === 'application/json') {
                options.body = JSON.stringify(options.body);
            }
        }

        return originalFetch(url, options);
    };

    // Configurar o mesmo para XMLHttpRequest (usado por algumas bibliotecas)
    const originalXhrOpen = XMLHttpRequest.prototype.open;
    const originalXhrSend = XMLHttpRequest.prototype.send;

    XMLHttpRequest.prototype.open = function(method, url) {
        this._method = method;
        originalXhrOpen.apply(this, arguments);
    };

    XMLHttpRequest.prototype.send = function(body) {
        if (this._method && ['POST', 'PUT', 'DELETE', 'PATCH'].includes(this._method.toUpperCase())) {
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

            if (csrfToken && csrfHeader) {
                this.setRequestHeader(csrfHeader, csrfToken);
            }
        }
        originalXhrSend.apply(this, arguments);
    };
});