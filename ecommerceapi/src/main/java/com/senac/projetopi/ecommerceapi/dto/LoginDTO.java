package com.senac.projetopi.ecommerceapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    public @Email(message = "Email inválido") @NotBlank(message = "Email é obrigatório") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Email inválido") @NotBlank(message = "Email é obrigatório") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Senha é obrigatória") String getSenha() {
        return senha;
    }

    public void setSenha(@NotBlank(message = "Senha é obrigatória") String senha) {
        this.senha = senha;
    }
}
