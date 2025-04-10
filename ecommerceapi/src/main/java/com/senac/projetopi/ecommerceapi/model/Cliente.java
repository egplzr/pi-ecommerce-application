package com.senac.projetopi.ecommerceapi.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Campo obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve conter entre 3 e 100 caracteres")
    @Pattern(regexp = "^(\\S+\\s+\\S+)$", message = "O nome completo deve ter pelo menos 2 palavras, cada uma com pelo menos 3 letras.")
    private String nome;

    @Email(message = "Email inválido")
    @NotBlank(message = "Campo obrigatório")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Campo obrigatório")
    @Column(unique = true)
    private String cpf;

    @NotBlank(message = "Campo obrigatório")
    private String senha;

    private LocalDateTime dataNascimento;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    @NotBlank(message = "Campo obrigatório")
    private String enderecoFaturamento;

    @ElementCollection
    private List<String> enderecosEntrega;

    @Column(nullable = false)
    private boolean ativo = true;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    public enum Genero {
        MASCULINO,
        FEMININO,
        OUTRO
    }

    // Getters e Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDateTime getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDateTime dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public String getEnderecoFaturamento() {
        return enderecoFaturamento;
    }

    public void setEnderecoFaturamento(String enderecoFaturamento) {
        this.enderecoFaturamento = enderecoFaturamento;
    }

    public List<String> getEnderecosEntrega() {
        return enderecosEntrega;
    }

    public void setEnderecosEntrega(List<String> enderecosEntrega) {
        this.enderecosEntrega = enderecosEntrega;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}

