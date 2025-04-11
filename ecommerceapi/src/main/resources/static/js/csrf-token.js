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
            const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

            options.headers[header] = token;
        }

        return originalFetch(url, options);
    };
});