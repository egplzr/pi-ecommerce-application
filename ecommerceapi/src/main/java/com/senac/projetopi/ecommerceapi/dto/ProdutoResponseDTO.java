package com.senac.projetopi.ecommerceapi.dto;

import com.senac.projetopi.ecommerceapi.model.ImagemProduto;
import com.senac.projetopi.ecommerceapi.model.Produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoResponseDTO {

    private Long id;
    private String codigo;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int quantidadeEstoque;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private List<ImagemProdutoDTO> imagens = new ArrayList<>();
    private String imagemPrincipalUrl;
    private BigDecimal avaliacao; // Novo campo

    public static ProdutoResponseDTO fromEntity(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setCodigo(produto.getCodigo());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setQuantidadeEstoque(produto.getQuantidadeEstoque());
        dto.setAvaliacao(produto.getAvaliacao()); // Propagar avaliação
        dto.setAtivo(produto.isAtivo());
        dto.setDataCriacao(produto.getDataCriacao());
        dto.setDataAtualizacao(produto.getDataAtualizacao());

        // Configurar imagem padrão
        dto.setImagemPrincipalUrl("/static/images/produto-sem-imagem.jpg");

        // Converter imagens
        if (produto.getImagens() != null && !produto.getImagens().isEmpty()) {
            dto.setImagens(produto.getImagens().stream()
                    .map(ImagemProdutoDTO::fromEntity)
                    .collect(Collectors.toList()));

            // Definir URL da imagem principal
            produto.getImagens().stream()
                    .filter(ImagemProduto::isPrincipal)
                    .findFirst()
                    .ifPresent(img -> {
                        // Garantir que o caminho começa com "/"
                        String caminho = img.getCaminho();
                        if (caminho != null && !caminho.isEmpty()) {
                            if (!caminho.startsWith("/")) {
                                caminho = "/" + caminho;
                            }
                            dto.setImagemPrincipalUrl(caminho);
                        }
                    });
        }

        return dto;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
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

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public List<ImagemProdutoDTO> getImagens() {
        return imagens;
    }

    public void setImagens(List<ImagemProdutoDTO> imagens) {
        this.imagens = imagens;
    }

    public String getImagemPrincipalUrl() {
        return imagemPrincipalUrl;
    }

    public void setImagemPrincipalUrl(String imagemPrincipalUrl) {
        this.imagemPrincipalUrl = imagemPrincipalUrl;
    }

    public BigDecimal getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(BigDecimal avaliacao) {
        this.avaliacao = avaliacao;
    }
}
