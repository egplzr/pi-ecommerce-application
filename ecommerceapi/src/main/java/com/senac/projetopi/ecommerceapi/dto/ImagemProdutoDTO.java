package com.senac.projetopi.ecommerceapi.dto;

import com.senac.projetopi.ecommerceapi.model.ImagemProduto;

public class ImagemProdutoDTO {

    private Long id;
    private String caminho;
    private String nomeArquivo;
    private boolean principal;

    public static ImagemProdutoDTO fromEntity(ImagemProduto imagem) {
        ImagemProdutoDTO dto = new ImagemProdutoDTO();
        dto.setId(imagem.getId());
        dto.setCaminho(imagem.getCaminho());
        dto.setNomeArquivo(imagem.getNomeArquivo());
        dto.setPrincipal(imagem.isPrincipal());
        return dto;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
}