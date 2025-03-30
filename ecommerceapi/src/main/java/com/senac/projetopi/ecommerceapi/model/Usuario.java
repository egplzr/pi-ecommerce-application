package com.senac.projetopi.ecommerceapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Campo obrigatório")
    @Size(max = 100, message = "Nome deve conter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Campo obrigatório")
    @Column(unique = true)
    private String cpf;

    @Email(message = "Email inválido")
    @NotBlank(message = "Campo obrigatório")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Campo obrigatório")
    private String senha;

    @Enumerated(EnumType.STRING)
    private GrupoUsuario grupo;

    private boolean ativo = true;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    public enum GrupoUsuario{
        ADMIN,
        ESTOQUISTA
    }

    public Usuario() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotBlank(message = "Campo obrigatório") @Size(max = 100, message = "Nome deve conter no máximo 100 caracteres") String getNome() {
        return nome;
    }

    public void setNome(@NotBlank(message = "Campo obrigatório") @Size(max = 100, message = "Nome deve conter no máximo 100 caracteres") String nome) {
        this.nome = nome;
    }

    public @NotBlank(message = "Campo obrigatório") String getCpf() {
        return cpf;
    }

    public void setCpf(@NotBlank(message = "Campo obrigatório") String cpf) {
        this.cpf = cpf;
    }

    public @Email(message = "Email inválido") @NotBlank(message = "Campo obrigatório") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Email inválido") @NotBlank(message = "Campo obrigatório") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Campo obrigatório") String getSenha() {
        return senha;
    }

    public void setSenha(@NotBlank(message = "Campo obrigatório") String senha) {
        this.senha = senha;
    }

    public GrupoUsuario getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoUsuario grupo) {
        this.grupo = grupo;
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
