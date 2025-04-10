package com.senac.projetopi.ecommerceapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

public class ClienteDTO {

    // Atualize a anotação para aceitar nomes com duas ou mais palavras, cada uma com pelo menos 3 letras.
    @NotBlank(message = "Campo obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve conter entre 3 e 100 caracteres")
    @Pattern(regexp = "^(?=\\S{3,}(?:\\s+\\S{3,})+).+$", message = "O nome completo deve ter pelo menos 2 palavras, cada uma com pelo menos 3 letras.")
    private String nome;

    @Email(message = "Email inválido")
    @NotBlank(message = "Campo obrigatório")
    private String email;

    @NotBlank(message = "Campo obrigatório")
    private String cpf;

    @NotBlank(message = "Campo obrigatório")
    private String senha;

    @NotBlank(message = "Campo obrigatório")
    private String enderecoFaturamento;

    private List<String> enderecosEntrega;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    private String genero;

    // Getters e Setters
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
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    public String getGenero() {
        return genero;
    }
    public void setGenero(String genero) {
        this.genero = genero;
    }
}
