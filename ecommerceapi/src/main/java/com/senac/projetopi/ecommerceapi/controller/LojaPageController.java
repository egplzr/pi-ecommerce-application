package com.senac.projetopi.ecommerceapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.senac.projetopi.ecommerceapi.dto.ProdutoResponseDTO;
import com.senac.projetopi.ecommerceapi.model.Produto;
import com.senac.projetopi.ecommerceapi.service.ProdutoService;

@Controller
@RequestMapping("/loja")
public class LojaPageController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String paginaInicial(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String codigo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        // Apenas produtos ativos são exibidos na loja
        Boolean ativo = true;

        Pageable pageable = PageRequest.of(page, size);
        Page<Produto> produtosPage = produtoService.buscarComFiltros(nome, codigo, ativo, pageable);

        // Converter para ProdutoResponseDTO
        Page<ProdutoResponseDTO> produtosDTO = produtosPage.map(ProdutoResponseDTO::fromEntity);

        model.addAttribute("produtos", produtosDTO);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", produtosPage.getTotalPages());
        model.addAttribute("filtroNome", nome);
        model.addAttribute("filtroCodigo", codigo);
        model.addAttribute("title", "Pagina Inicial");
        model.addAttribute("content", "~{loja/home :: content}");


        return "loja/base";
    }

    @GetMapping("/produto/{id}")
    public String paginaDetalhesProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id);

        // Converter para DTO
        ProdutoResponseDTO produtoDTO = ProdutoResponseDTO.fromEntity(produto);

        model.addAttribute("produto", produtoDTO);
        model.addAttribute("title", produtoDTO.getNome() + " - Detalhes do Produto");
        model.addAttribute("content", "loja/produto-detalhes :: content");

        return "loja/base";
    }
}