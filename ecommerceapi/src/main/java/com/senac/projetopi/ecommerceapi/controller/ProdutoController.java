package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.dto.ProdutoDTO;
import com.senac.projetopi.ecommerceapi.dto.ProdutoResponseDTO;
import com.senac.projetopi.ecommerceapi.model.ImagemProduto;
import com.senac.projetopi.ecommerceapi.model.Produto;
import com.senac.projetopi.ecommerceapi.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarProdutos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<Produto> produtos = produtoService.buscarComFiltros(nome, codigo, ativo, pageable);
        Page<ProdutoResponseDTO> produtosDTO = produtos.map(ProdutoResponseDTO::fromEntity);

        return ResponseEntity.ok(produtosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        ProdutoResponseDTO produtoDTO = ProdutoResponseDTO.fromEntity(produto);
        return ResponseEntity.ok(produtoDTO);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorCodigo(@PathVariable String codigo) {
        Produto produto = produtoService.buscarPorCodigo(codigo);
        ProdutoResponseDTO produtoDTO = ProdutoResponseDTO.fromEntity(produto);
        return ResponseEntity.ok(produtoDTO);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@Valid @RequestBody ProdutoDTO produtoDTO) {
        Produto novoProduto = produtoService.criar(produtoDTO);
        ProdutoResponseDTO resposta = ProdutoResponseDTO.fromEntity(novoProduto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoDTO produtoDTO) {

        Produto produtoAtualizado = produtoService.atualizar(id, produtoDTO);
        ProdutoResponseDTO resposta = ProdutoResponseDTO.fromEntity(produtoAtualizado);
        return ResponseEntity.ok(resposta);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    public ResponseEntity<ProdutoResponseDTO> alternarStatusProduto(@PathVariable Long id) {
        Produto produto = produtoService.alternarStatus(id);
        ProdutoResponseDTO resposta = ProdutoResponseDTO.fromEntity(produto);
        return ResponseEntity.ok(resposta);
    }

    @PostMapping(value = "/{id}/imagens", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    public ResponseEntity<?> adicionarImagem(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem,
            @RequestParam(value = "principal", defaultValue = "false") boolean principal) {

        ImagemProduto novaImagem = produtoService.adicionarImagem(id, imagem, principal);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("id", novaImagem.getId());
        resposta.put("caminho", novaImagem.getCaminho());
        resposta.put("nomeArquivo", novaImagem.getNomeArquivo());
        resposta.put("principal", novaImagem.isPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PatchMapping("/{produtoId}/imagens/{imagemId}/principal")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    public ResponseEntity<?> definirImagemPrincipal(
            @PathVariable Long produtoId,
            @PathVariable Long imagemId) {

        produtoService.definirImagemPrincipal(produtoId, imagemId);

        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Imagem definida como principal com sucesso");

        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/{produtoId}/imagens/{imagemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    public ResponseEntity<?> removerImagem(
            @PathVariable Long produtoId,
            @PathVariable Long imagemId) {

        produtoService.removerImagem(produtoId, imagemId);

        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Imagem removida com sucesso");

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{produtoId}/imagens")
    public ResponseEntity<List<Map<String, Object>>> listarImagensProduto(@PathVariable Long produtoId) {
        List<ImagemProduto> imagens = produtoService.listarImagensProduto(produtoId);

        List<Map<String, Object>> listaImagens = imagens.stream()
                .map(img -> {
                    Map<String, Object> imagemMap = new HashMap<>();
                    imagemMap.put("id", img.getId());
                    imagemMap.put("caminho", img.getCaminho());
                    imagemMap.put("nomeArquivo", img.getNomeArquivo());
                    imagemMap.put("principal", img.isPrincipal());
                    return imagemMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaImagens);
    }
}