package com.senac.projetopi.ecommerceapi.dto;

import jakarta.validation.constraints.NotBlank;

public class EnderecoDTO {
    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;
    private boolean padrao;

    // Getters e setters
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public boolean isPadrao() { return padrao; }
    public void setPadrao(boolean padrao) { this.padrao = padrao; }
}