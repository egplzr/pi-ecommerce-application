// Script para a loja virtual

// Inicializar ao carregar a página
document.addEventListener('DOMContentLoaded', function() {
    // Carregar a contagem de itens do carrinho
    atualizarContagemCarrinho();

    // Adicionar evento aos botões de adicionar ao carrinho
    const addToCartButtons = document.querySelectorAll('.add-to-cart');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            const produtoId = this.getAttribute('data-produto-id');
            adicionarAoCarrinho(produtoId, 1);

            // Efeito visual
            this.classList.add('cart-animation');
            setTimeout(() => {
                this.classList.remove('cart-animation');
            }, 500);
        });
    });
});

// Função para adicionar produto ao carrinho
function adicionarAoCarrinho(produtoId, quantidade) {
    // Obter carrinho atual do localStorage
    let carrinho = JSON.parse(localStorage.getItem('carrinho')) || { itens: [] };

    // Verificar se o produto já está no carrinho
    const itemExistente = carrinho.itens.find(item => item.produtoId === produtoId);

    if (itemExistente) {
        // Atualizar quantidade
        itemExistente.quantidade += parseInt(quantidade);
    } else {
        // Adicionar novo item
        carrinho.itens.push({
            produtoId: produtoId,
            quantidade: parseInt(quantidade)
        });
    }

    // Salvar carrinho atualizado
    localStorage.setItem('carrinho', JSON.stringify(carrinho));

    // Atualizar contador de itens
    atualizarContagemCarrinho();

    // Mostrar feedback
    mostrarMensagem('Produto adicionado ao carrinho!', 'success');
}

// Função para atualizar a contagem de itens no carrinho
function atualizarContagemCarrinho() {
    const carrinho = JSON.parse(localStorage.getItem('carrinho')) || { itens: [] };
    const totalItens = carrinho.itens.reduce((total, item) => total + item.quantidade, 0);

    document.getElementById('cartItemCount').textContent = totalItens;
}

// Função para mostrar mensagens de feedback
function mostrarMensagem(mensagem, tipo) {
    // Criar elemento de alerta
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${tipo} alert-dismissible fade show position-fixed top-0 end-0 m-3`;
    alertDiv.setAttribute('role', 'alert');
    alertDiv.innerHTML = `
        ${mensagem}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
    `;

    // Adicionar ao corpo do documento
    document.body.appendChild(alertDiv);

    // Remover após alguns segundos
    setTimeout(() => {
        const bsAlert = new bootstrap.Alert(alertDiv);
        bsAlert.close();
    }, 3000);
}

// Funções para incrementar/decrementar quantidade
function incrementQuantity() {
    const input = document.getElementById('quantidade');
    const maxValue = parseInt(input.getAttribute('max') || 999);
    let currentValue = parseInt(input.value || 1);

    if (currentValue < maxValue) {
        input.value = currentValue + 1;
    }
}

function decrementQuantity() {
    const input = document.getElementById('quantidade');
    let currentValue = parseInt(input.value || 1);

    if (currentValue > 1) {
        input.value = currentValue - 1;
    }
}

// Função para atualizar a contagem de itens no carrinho
function atualizarContagemCarrinho() {
    fetch('/api/carrinho')
        .then(response => response.json())
        .then(data => {
            document.getElementById('cartItemCount').textContent = data.quantidadeItens;
        })
        .catch(error => {
            console.error('Erro ao atualizar contagem do carrinho:', error);
        });
}

// Chamar no carregamento da página
document.addEventListener('DOMContentLoaded', function() {
    atualizarContagemCarrinho();
});