package com.senac.projetopi.ecommerceapi.service;

import com.senac.projetopi.ecommerceapi.dto.ProdutoDTO;
import com.senac.projetopi.ecommerceapi.exception.RecursoNaoEncontradoException;
import com.senac.projetopi.ecommerceapi.exception.ValidacaoException;
import com.senac.projetopi.ecommerceapi.model.Produto;
import com.senac.projetopi.ecommerceapi.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários de ProdutoService")
public class ProdutoServiceTest {

    @InjectMocks
    private ProdutoService produtoService;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ArmazenamentoService armazenamentoService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private ProdutoDTO produtoDTOAdmin;
    private ProdutoDTO produtoDTOEstoquista;
    private Produto produtoDoBanco;

    private final Long ID_PRODUTO_EXISTENTE = 1L;
    private final Long ID_PRODUTO_INEXISTENTE = 99L;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        produtoDTOAdmin = new ProdutoDTO();
        produtoDTOAdmin.setCodigo("PROD_ADM");
        produtoDTOAdmin.setNome("Produto Admin Criado");
        produtoDTOAdmin.setDescricao("Desc do produto criado pelo admin");
        produtoDTOAdmin.setPreco(new BigDecimal("120.50"));
        produtoDTOAdmin.setQuantidadeEstoque(20);
        produtoDTOAdmin.setAtivo(true);
        produtoDTOAdmin.setAvaliacao(new BigDecimal("4.8"));

        produtoDTOEstoquista = new ProdutoDTO();
        produtoDTOEstoquista.setCodigo("PROD_EST_COD");
        produtoDTOEstoquista.setNome("Nome Ignorado pelo Estoquista");
        produtoDTOEstoquista.setDescricao("Desc Ignorada pelo Estoquista");
        produtoDTOEstoquista.setPreco(new BigDecimal("300.00"));
        produtoDTOEstoquista.setQuantidadeEstoque(50);
        produtoDTOEstoquista.setAtivo(false);
        produtoDTOEstoquista.setAvaliacao(new BigDecimal("2.5"));

        produtoDoBanco = new Produto();
        produtoDoBanco.setId(ID_PRODUTO_EXISTENTE);
        produtoDoBanco.setCodigo("PROD_ORIGINAL");
        produtoDoBanco.setNome("Produto Original do Banco");
        produtoDoBanco.setDescricao("Descrição original do banco");
        produtoDoBanco.setPreco(new BigDecimal("99.99"));
        produtoDoBanco.setQuantidadeEstoque(15);
        produtoDoBanco.setAtivo(true);
        produtoDoBanco.setAvaliacao(new BigDecimal("3.0"));
    }

    private void mockUserWithRole(String email, String role) {
        lenient().when(authentication.getName()).thenReturn(email);
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        lenient().doReturn(authorities).when(authentication).getAuthorities();
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockGenericAuthenticatedUser() {
        lenient().when(authentication.getName()).thenReturn("generic@example.com");
        lenient().doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso por ADMIN")
    void deveCriarProdutoComSucessoPorAdmin() {
        mockUserWithRole("admin@example.com", "ROLE_ADMIN");

        when(produtoRepository.existsByCodigo(produtoDTOAdmin.getCodigo())).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto produtoSalvo = invocation.getArgument(0);
            produtoSalvo.setId(ID_PRODUTO_EXISTENTE);
            return produtoSalvo;
        });

        Produto produtoCriado = produtoService.criar(produtoDTOAdmin);

        assertNotNull(produtoCriado);
        assertEquals(ID_PRODUTO_EXISTENTE, produtoCriado.getId());
        assertEquals(produtoDTOAdmin.getCodigo(), produtoCriado.getCodigo());
        assertEquals(produtoDTOAdmin.getNome(), produtoCriado.getNome());
        assertEquals(0, produtoDTOAdmin.getPreco().compareTo(produtoCriado.getPreco()));
        assertEquals(produtoDTOAdmin.getQuantidadeEstoque(), produtoCriado.getQuantidadeEstoque());
        assertTrue(produtoCriado.isAtivo());
        assertEquals(0, produtoDTOAdmin.getAvaliacao().compareTo(produtoCriado.getAvaliacao()));

        verify(produtoRepository, times(1)).existsByCodigo(produtoDTOAdmin.getCodigo());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao tentar criar produto com código já existente")
    void deveLancarExcecaoQuandoCodigoJaExistenteAoCriar() {
        mockGenericAuthenticatedUser();
        when(produtoRepository.existsByCodigo(produtoDTOAdmin.getCodigo())).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            produtoService.criar(produtoDTOAdmin);
        });

        assertEquals("Já existe um produto com o código: " + produtoDTOAdmin.getCodigo(), exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar o produto completamente por ADMIN")
    void deveAtualizarProdutoCompletamentePorAdmin() {
        mockUserWithRole("admin@example.com", "ROLE_ADMIN");
        when(produtoRepository.findById(ID_PRODUTO_EXISTENTE)).thenReturn(Optional.of(produtoDoBanco));
        when(produtoRepository.existsByCodigo("PROD_ATUALIZADO")).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoDTO dtoParaAtualizarAdmin = new ProdutoDTO();
        dtoParaAtualizarAdmin.setCodigo("PROD_ATUALIZADO");
        dtoParaAtualizarAdmin.setNome("Nome Atualizado Admin");
        dtoParaAtualizarAdmin.setDescricao("Nova Descrição Admin");
        dtoParaAtualizarAdmin.setPreco(new BigDecimal("250.00"));
        dtoParaAtualizarAdmin.setQuantidadeEstoque(30);
        dtoParaAtualizarAdmin.setAtivo(false);
        dtoParaAtualizarAdmin.setAvaliacao(new BigDecimal("5.0"));

        Produto produtoAtualizado = produtoService.atualizar(ID_PRODUTO_EXISTENTE, dtoParaAtualizarAdmin);

        assertNotNull(produtoAtualizado);
        assertEquals(ID_PRODUTO_EXISTENTE, produtoAtualizado.getId());
        assertEquals(dtoParaAtualizarAdmin.getCodigo(), produtoAtualizado.getCodigo());
        assertEquals(dtoParaAtualizarAdmin.getNome(), produtoAtualizado.getNome());
        assertEquals(0, dtoParaAtualizarAdmin.getPreco().compareTo(produtoAtualizado.getPreco()));
        assertEquals(dtoParaAtualizarAdmin.getQuantidadeEstoque(), produtoAtualizado.getQuantidadeEstoque());
        assertFalse(produtoAtualizado.isAtivo());
        assertEquals(0, dtoParaAtualizarAdmin.getAvaliacao().compareTo(produtoAtualizado.getAvaliacao()));

        verify(produtoRepository, times(1)).findById(ID_PRODUTO_EXISTENTE);
        verify(produtoRepository, times(1)).existsByCodigo(dtoParaAtualizarAdmin.getCodigo());
        verify(produtoRepository, times(1)).save(produtoDoBanco);
    }

    @Test
    @DisplayName("Deve atualizar apenas a quantidade em estoque por ESTOQUISTA")
    void deveAtualizarApenasQuantidadePorEstoquista() {
        mockUserWithRole("estoquista@example.com", "ROLE_ESTOQUISTA");
        when(produtoRepository.findById(ID_PRODUTO_EXISTENTE)).thenReturn(Optional.of(produtoDoBanco));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoDTO dtoParaEstoquista = new ProdutoDTO();
        dtoParaEstoquista.setCodigo(produtoDoBanco.getCodigo());
        dtoParaEstoquista.setNome("Nome Ignorado");
        dtoParaEstoquista.setDescricao("Desc Ignorada");
        dtoParaEstoquista.setPreco(new BigDecimal("500.00"));
        dtoParaEstoquista.setQuantidadeEstoque(50);
        dtoParaEstoquista.setAtivo(false);
        dtoParaEstoquista.setAvaliacao(new BigDecimal("1.0"));

        String codigoOriginal = produtoDoBanco.getCodigo();
        String nomeOriginal = produtoDoBanco.getNome();
        BigDecimal precoOriginal = produtoDoBanco.getPreco();
        boolean ativoOriginal = produtoDoBanco.isAtivo();
        BigDecimal avaliacaoOriginal = produtoDoBanco.getAvaliacao();

        Produto produtoAtualizado = produtoService.atualizar(ID_PRODUTO_EXISTENTE, dtoParaEstoquista);

        assertNotNull(produtoAtualizado);
        assertEquals(ID_PRODUTO_EXISTENTE, produtoAtualizado.getId());
        assertEquals(dtoParaEstoquista.getQuantidadeEstoque(), produtoAtualizado.getQuantidadeEstoque());
        assertEquals(codigoOriginal, produtoAtualizado.getCodigo());
        assertEquals(nomeOriginal, produtoAtualizado.getNome());
        assertEquals(0, precoOriginal.compareTo(produtoAtualizado.getPreco()));
        assertEquals(ativoOriginal, produtoAtualizado.isAtivo());
        assertEquals(0, avaliacaoOriginal.compareTo(produtoAtualizado.getAvaliacao()));

        verify(produtoRepository, times(1)).findById(ID_PRODUTO_EXISTENTE);
        verify(produtoRepository, never()).existsByCodigo(anyString());
        verify(produtoRepository, times(1)).save(produtoDoBanco);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar atualizar produto inexistente")
    void deveLancarExcecaoQuandoProdutoInexistenteAoAtualizar() {
        mockGenericAuthenticatedUser();
        when(produtoRepository.findById(ID_PRODUTO_INEXISTENTE)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            produtoService.atualizar(ID_PRODUTO_INEXISTENTE, produtoDTOAdmin);
        });

        assertEquals("Produto não encontrado com ID: " + ID_PRODUTO_INEXISTENTE, exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
        verify(produtoRepository, times(1)).findById(ID_PRODUTO_INEXISTENTE);
        // Removido verify de getAuthorities() pois não é chamado quando produto não é encontrado
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao tentar atualizar produto com código para um já existente")
    void deveLancarExcecaoQuandoAtualizarProdutoComCodigoExistente() {
        mockGenericAuthenticatedUser();
        when(produtoRepository.findById(ID_PRODUTO_EXISTENTE)).thenReturn(Optional.of(produtoDoBanco));

        ProdutoDTO dtoComCodigoDuplicado = new ProdutoDTO();
        dtoComCodigoDuplicado.setCodigo("COD_JA_EXISTENTE_NOVO");
        dtoComCodigoDuplicado.setNome("Nome Qualquer");
        dtoComCodigoDuplicado.setPreco(BigDecimal.ONE);
        dtoComCodigoDuplicado.setQuantidadeEstoque(1);

        when(produtoRepository.existsByCodigo(dtoComCodigoDuplicado.getCodigo())).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            produtoService.atualizar(ID_PRODUTO_EXISTENTE, dtoComCodigoDuplicado);
        });

        assertEquals("Já existe um produto com o código: " + dtoComCodigoDuplicado.getCodigo(), exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
        verify(produtoRepository, times(1)).findById(ID_PRODUTO_EXISTENTE);
        verify(produtoRepository, times(1)).existsByCodigo(dtoComCodigoDuplicado.getCodigo());
    }

    @Test
    @DisplayName("Deve alternar o status do produto para inativo com sucesso")
    void deveAlternarStatusParaInativoComSucesso() {
        produtoDoBanco.setAtivo(true);
        when(produtoRepository.findById(ID_PRODUTO_EXISTENTE)).thenReturn(Optional.of(produtoDoBanco));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoDoBanco);

        Produto produtoAlterado = produtoService.alternarStatus(ID_PRODUTO_EXISTENTE);

        assertNotNull(produtoAlterado);
        assertFalse(produtoAlterado.isAtivo());
        verify(produtoRepository, times(1)).findById(ID_PRODUTO_EXISTENTE);
        verify(produtoRepository, times(1)).save(produtoDoBanco);
    }

    @Test
    @DisplayName("Deve alternar o status do produto para ativo com sucesso")
    void deveAlternarStatusParaAtivoComSucesso() {
        produtoDoBanco.setAtivo(false);
        when(produtoRepository.findById(ID_PRODUTO_EXISTENTE)).thenReturn(Optional.of(produtoDoBanco));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoDoBanco);

        Produto produtoAlterado = produtoService.alternarStatus(ID_PRODUTO_EXISTENTE);

        assertNotNull(produtoAlterado);
        assertTrue(produtoAlterado.isAtivo());
        verify(produtoRepository, times(1)).findById(ID_PRODUTO_EXISTENTE);
        verify(produtoRepository, times(1)).save(produtoDoBanco);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar alternar status de produto inexistente")
    void deveLancarExcecaoQuandoProdutoInexistenteAoAlternarStatus() {
        when(produtoRepository.findById(ID_PRODUTO_INEXISTENTE)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            produtoService.alternarStatus(ID_PRODUTO_INEXISTENTE);
        });

        assertEquals("Produto não encontrado com ID: " + ID_PRODUTO_INEXISTENTE, exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
        verify(produtoRepository, times(1)).findById(ID_PRODUTO_INEXISTENTE);
    }
}