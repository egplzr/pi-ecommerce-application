package com.senac.projetopi.ecommerceapi.service;

import com.senac.projetopi.ecommerceapi.exception.RecursoNaoEncontradoException;
import com.senac.projetopi.ecommerceapi.exception.ValidacaoException;
import com.senac.projetopi.ecommerceapi.model.Usuario;
import com.senac.projetopi.ecommerceapi.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários de UsuarioService")
public class UsuariosServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValidadorCpfService validadorCpfService;

    // Mocks para simular o contexto de segurança (usuário logado)
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private Usuario usuarioValido;
    private Usuario usuarioParaAtualizar;
    private final Long ID_USUARIO_EXISTENTE = 1L;
    private final Long ID_USUARIO_INEXISTENTE = 99L;

    @BeforeEach
    void setUp() {
        // Reinicializa o usuário válido para cada teste
        usuarioValido = new Usuario();
        usuarioValido.setId(ID_USUARIO_EXISTENTE);
        usuarioValido.setNome("Teste Usuário");
        usuarioValido.setEmail("teste@example.com");
        usuarioValido.setCpf("12345678909");
        usuarioValido.setSenha("senha_encriptada"); // Já encriptada para simular dados do banco
        usuarioValido.setGrupo(Usuario.GrupoUsuario.ADMIN);
        usuarioValido.setAtivo(true);

        usuarioParaAtualizar = new Usuario();
        usuarioParaAtualizar.setNome("Nome Atualizado");
        usuarioParaAtualizar.setEmail("teste@example.com"); // Email não é alterável via DTO no serviço
        usuarioParaAtualizar.setCpf("98765432100");
        usuarioParaAtualizar.setSenha("nova_senha123");
        usuarioParaAtualizar.setGrupo(Usuario.GrupoUsuario.ESTOQUISTA); // Tentando mudar o grupo
    }

    // --- Testes para o método 'criar' (mantidos do exemplo anterior) ---
    @Test
    @DisplayName("Deve criar um usuário com sucesso quando os dados são válidos")
    void deveCriarUsuarioComSucesso() {
        when(usuarioRepository.existsByEmail(usuarioValido.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(usuarioValido.getCpf())).thenReturn(false);
        when(validadorCpfService.validar(usuarioValido.getCpf())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("senha_encriptada"); // qualquer string
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioValido);

        Usuario usuarioCriado = usuarioService.criar(usuarioValido);

        assertNotNull(usuarioCriado);
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(validadorCpfService, times(1)).validar(anyString());
        verify(usuarioRepository, times(1)).save(usuarioValido);
        assertTrue(usuarioCriado.isAtivo());
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao tentar criar usuário com email já cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        when(usuarioRepository.existsByEmail(usuarioValido.getEmail())).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            usuarioService.criar(usuarioValido);
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao tentar criar usuário com CPF já cadastrado")
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        when(usuarioRepository.existsByEmail(usuarioValido.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(usuarioValido.getCpf())).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            usuarioService.criar(usuarioValido);
        });

        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao tentar criar usuário com CPF inválido")
    void deveLancarExcecaoQuandoCpfInvalido() {
        when(usuarioRepository.existsByEmail(usuarioValido.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(usuarioValido.getCpf())).thenReturn(false);
        when(validadorCpfService.validar(usuarioValido.getCpf())).thenReturn(false);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            usuarioService.criar(usuarioValido);
        });

        assertEquals("CPF inválido", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // --- Novos testes para o método 'atualizar' ---

    @Test
    @DisplayName("Deve atualizar o usuário com sucesso quando os dados são válidos e não é o próprio usuário")
    void deveAtualizarUsuarioComSucessoQuandoDadosValidosENaoEhProprioUsuario() {
        // Cenário: Usuário existente e logado como outro ADMIN
        when(usuarioRepository.findById(ID_USUARIO_EXISTENTE)).thenReturn(Optional.of(usuarioValido));
        // CPF pode mudar, então validar o novo CPF
        when(validadorCpfService.validar(usuarioParaAtualizar.getCpf())).thenReturn(true);
        when(passwordEncoder.encode(usuarioParaAtualizar.getSenha())).thenReturn("nova_senha_encriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioParaAtualizar);

        // Simular usuário logado diferente do que está sendo atualizado
        when(authentication.getName()).thenReturn("outro_admin@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Ação
        Usuario usuarioAtualizado = usuarioService.atualizar(ID_USUARIO_EXISTENTE, usuarioParaAtualizar);

        // Verificação
        assertNotNull(usuarioAtualizado);
        assertEquals(usuarioParaAtualizar.getNome(), usuarioAtualizado.getNome());
        assertEquals(usuarioParaAtualizar.getCpf(), usuarioAtualizado.getCpf());
        // Grupo pode ser atualizado se não for o próprio usuário
        assertEquals(usuarioParaAtualizar.getGrupo(), usuarioAtualizado.getGrupo());
        verify(passwordEncoder, times(1)).encode(usuarioParaAtualizar.getSenha());
        verify(usuarioRepository, times(1)).save(usuarioValido);
        verify(validadorCpfService, times(1)).validar(usuarioParaAtualizar.getCpf()); // CPF foi validado
    }

    @Test
    @DisplayName("Deve atualizar o usuário com sucesso quando os dados são válidos e é o próprio usuário logado")
    void deveAtualizarUsuarioComSucessoQuandoDadosValidosEProprioUsuario() {
        // Cenário: Usuário existente é o próprio usuário logado
        when(usuarioRepository.findById(ID_USUARIO_EXISTENTE)).thenReturn(Optional.of(usuarioValido));
        when(validadorCpfService.validar(usuarioParaAtualizar.getCpf())).thenReturn(true);
        when(passwordEncoder.encode(usuarioParaAtualizar.getSenha())).thenReturn("nova_senha_encriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioValido);

        // Simular o próprio usuário logado
        when(authentication.getName()).thenReturn(usuarioValido.getEmail());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Ação
        Usuario usuarioAtualizado = usuarioService.atualizar(ID_USUARIO_EXISTENTE, usuarioParaAtualizar);

        // Verificação
        assertNotNull(usuarioAtualizado);
        assertEquals(usuarioParaAtualizar.getNome(), usuarioAtualizado.getNome());
        assertEquals(usuarioParaAtualizar.getCpf(), usuarioAtualizado.getCpf());
        // Grupo NÃO deve ser atualizado se for o próprio usuário
        assertEquals(usuarioValido.getGrupo(), usuarioAtualizado.getGrupo()); // O grupo permanece o original
        verify(passwordEncoder, times(1)).encode(usuarioParaAtualizar.getSenha());
        verify(usuarioRepository, times(1)).save(usuarioValido);
        verify(validadorCpfService, times(1)).validar(usuarioParaAtualizar.getCpf());
    }

    @Test
    @DisplayName("Deve atualizar o usuário sem alterar a senha se a senha for nula ou vazia")
    void deveAtualizarUsuarioSemAlterarSenhaQuandoSenhaNulaOuVazia() {
        // Cenário: Usuário existente e senha nula
        when(usuarioRepository.findById(ID_USUARIO_EXISTENTE)).thenReturn(Optional.of(usuarioValido));
        when(validadorCpfService.validar(usuarioParaAtualizar.getCpf())).thenReturn(true);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioValido);

        usuarioParaAtualizar.setSenha(null); // Senha nula

        // Simular usuário logado
        when(authentication.getName()).thenReturn("outro_admin@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Ação
        Usuario usuarioAtualizado = usuarioService.atualizar(ID_USUARIO_EXISTENTE, usuarioParaAtualizar);

        // Verificação
        assertNotNull(usuarioAtualizado);
        // A senha não deve ser encriptada ou alterada se for nula/vazia
        verify(passwordEncoder, never()).encode(anyString());
        assertEquals(usuarioValido.getSenha(), usuarioAtualizado.getSenha()); // A senha deve ser a original
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar atualizar usuário inexistente")
    void deveLancarExcecaoQuandoUsuarioInexistenteAoAtualizar() {
        // Cenário: Usuário não encontrado no repositório.
        when(usuarioRepository.findById(ID_USUARIO_INEXISTENTE)).thenReturn(Optional.empty());

        // Ação e Verificação: Esperar RecursoNaoEncontradoException.
        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            usuarioService.atualizar(ID_USUARIO_INEXISTENTE, usuarioParaAtualizar);
        });

        assertEquals("Usuário não encontrado com ID: " + ID_USUARIO_INEXISTENTE, exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class)); // Verificar que save não foi chamado
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao tentar atualizar usuário com CPF inválido")
    void deveLancarExcecaoQuandoCpfInvalidoAoAtualizar() {
        // Cenário: Usuário existente, mas o novo CPF é inválido.
        when(usuarioRepository.findById(ID_USUARIO_EXISTENTE)).thenReturn(Optional.of(usuarioValido));
        // Simula que o CPF é diferente do original E que é inválido
        when(validadorCpfService.validar(usuarioParaAtualizar.getCpf())).thenReturn(false);

        // Simular usuário logado
        when(authentication.getName()).thenReturn("outro_admin@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Ação e Verificação: Esperar ValidacaoException.
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            usuarioService.atualizar(ID_USUARIO_EXISTENTE, usuarioParaAtualizar);
        });

        assertEquals("CPF inválido", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class)); // Verificar que save não foi chamado
    }

    // --- Novos testes para o método 'alternarStatus' ---

    @Test
    @DisplayName("Deve alternar o status do usuário para inativo com sucesso")
    void deveAlternarStatusParaInativoComSucesso() {
        // Cenário: Usuário existe e está ativo, e não é o usuário logado
        usuarioValido.setAtivo(true); // Garante que o usuário está ativo inicialmente
        when(usuarioRepository.findById(ID_USUARIO_EXISTENTE)).thenReturn(Optional.of(usuarioValido));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioValido);

        // Simular que o usuário logado é diferente do que está sendo alterado
        when(authentication.getName()).thenReturn("outro_admin@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Ação
        Usuario usuarioAlterado = usuarioService.alternarStatus(ID_USUARIO_EXISTENTE);

        // Verificação
        assertNotNull(usuarioAlterado);
        assertFalse(usuarioAlterado.isAtivo()); // Deve estar inativo
        verify(usuarioRepository, times(1)).save(usuarioValido);
    }

    @Test
    @DisplayName("Deve alternar o status do usuário para ativo com sucesso")
    void deveAlternarStatusParaAtivoComSucesso() {
        // Cenário: Usuário existe e está inativo, e não é o usuário logado
        usuarioValido.setAtivo(false); // Garante que o usuário está inativo inicialmente
        when(usuarioRepository.findById(ID_USUARIO_EXISTENTE)).thenReturn(Optional.of(usuarioValido));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioValido);

        // Simular que o usuário logado é diferente do que está sendo alterado
        when(authentication.getName()).thenReturn("outro_admin@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Ação
        Usuario usuarioAlterado = usuarioService.alternarStatus(ID_USUARIO_EXISTENTE);

        // Verificação
        assertNotNull(usuarioAlterado);
        assertTrue(usuarioAlterado.isAtivo()); // Deve estar ativo
        verify(usuarioRepository, times(1)).save(usuarioValido);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar alternar status de usuário inexistente")
    void deveLancarExcecaoQuandoUsuarioInexistenteAoAlternarStatus() {
        // Cenário: Usuário não encontrado no repositório.
        when(usuarioRepository.findById(ID_USUARIO_INEXISTENTE)).thenReturn(Optional.empty());

        // Ação e Verificação: Esperar RecursoNaoEncontradoException.
        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            usuarioService.alternarStatus(ID_USUARIO_INEXISTENTE);
        });

        assertEquals("Usuário não encontrado com ID: " + ID_USUARIO_INEXISTENTE, exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class)); // Verificar que save não foi chamado
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao tentar alternar status do próprio usuário logado")
    void deveLancarExcecaoQuandoTentarAlternarStatusDoProprioUsuario() {
        // Cenário: Usuário que está sendo alterado é o mesmo usuário logado.
        when(usuarioRepository.findById(ID_USUARIO_EXISTENTE)).thenReturn(Optional.of(usuarioValido));

        // Simular que o usuário logado é o mesmo que está sendo alterado
        when(authentication.getName()).thenReturn(usuarioValido.getEmail());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Ação e Verificação: Esperar ValidacaoException.
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            usuarioService.alternarStatus(ID_USUARIO_EXISTENTE);
        });

        assertEquals("Não é permitido alterar o status do próprio usuário logado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class)); // Verificar que save não foi chamado
    }
}