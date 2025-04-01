package com.senac.projetopi.ecommerceapi.service;

import com.senac.projetopi.ecommerceapi.dto.ProdutoDTO;
import com.senac.projetopi.ecommerceapi.exception.RecursoNaoEncontradoException;
import com.senac.projetopi.ecommerceapi.exception.ValidacaoException;
import com.senac.projetopi.ecommerceapi.model.ImagemProduto;
import com.senac.projetopi.ecommerceapi.model.Produto;
import com.senac.projetopi.ecommerceapi.repository.ImagemProdutoRepository;
import com.senac.projetopi.ecommerceapi.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ImagemProdutoRepository imagemProdutoRepository;

    @Autowired
    private ArmazenamentoService armazenamentoService;

    @Transactional(readOnly = true)
    public Page<Produto> buscarTodos(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Produto> buscarComFiltros(String nome, String codigo, Boolean ativo, Pageable pageable) {
        return produtoRepository.buscarComFiltros(nome, codigo, ativo, pageable);
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Produto buscarPorCodigo(String codigo) {
        return produtoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com código: " + codigo));
    }

    @Transactional
    public Produto criar(ProdutoDTO produtoDTO) {
        validarProduto(produtoDTO);

        Produto produto = new Produto();
        popularProdutoComDTO(produto, produtoDTO);

        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, ProdutoDTO produtoDTO) {
        Produto produtoExistente = buscarPorId(id);

        // Verificar se o código foi alterado e se já existe
        if (!produtoExistente.getCodigo().equals(produtoDTO.getCodigo()) &&
                produtoRepository.existsByCodigo(produtoDTO.getCodigo())) {
            throw new ValidacaoException("Já existe um produto com o código: " + produtoDTO.getCodigo());
        }

        popularProdutoComDTO(produtoExistente, produtoDTO);

        return produtoRepository.save(produtoExistente);
    }

    @Transactional
    public Produto alternarStatus(Long id) {
        Produto produto = buscarPorId(id);
        produto.setAtivo(!produto.isAtivo());
        return produtoRepository.save(produto);
    }

    @Transactional
    public ImagemProduto adicionarImagem(Long produtoId, MultipartFile arquivo, boolean principal) {
        Produto produto = buscarPorId(produtoId);

        // Armazenar o arquivo
        String caminho = armazenamentoService.armazenarArquivo(arquivo);

        // Criar a entidade de imagem
        ImagemProduto imagem = new ImagemProduto();
        imagem.setCaminho(caminho);
        imagem.setNomeArquivo(arquivo.getOriginalFilename());
        imagem.setPrincipal(principal);
        imagem.setProduto(produto);

        // Se esta imagem for marcada como principal, desmarcar as outras
        if (principal) {
            produto.getImagens().forEach(img -> img.setPrincipal(false));
        }

        // Adicionar a imagem ao produto
        produto.adicionarImagem(imagem);
        produtoRepository.save(produto);

        return imagem;
    }

    @Transactional
    public void definirImagemPrincipal(Long produtoId, Long imagemId) {
        Produto produto = buscarPorId(produtoId);

        // Verificar se a imagem pertence ao produto
        boolean imagemEncontrada = false;
        for (ImagemProduto imagem : produto.getImagens()) {
            if (imagem.getId().equals(imagemId)) {
                imagemEncontrada = true;
                break;
            }
        }

        if (!imagemEncontrada) {
            throw new RecursoNaoEncontradoException("Imagem não encontrada para este produto");
        }

        // Definir a imagem como principal
        produto.definirImagemPrincipal(imagemId);
        produtoRepository.save(produto);
    }

    @Transactional
    public void removerImagem(Long produtoId, Long imagemId) {
        Produto produto = buscarPorId(produtoId);

        // Encontrar a imagem para remoção
        ImagemProduto imagem = produto.getImagens().stream()
                .filter(img -> img.getId().equals(imagemId))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Imagem não encontrada com ID: " + imagemId));

        // Se a imagem for principal e não for a única, definir outra como principal
        if (imagem.isPrincipal() && produto.getImagens().size() > 1) {
            Optional<ImagemProduto> outraImagem = produto.getImagens().stream()
                    .filter(img -> !img.getId().equals(imagemId))
                    .findFirst();

            if (outraImagem.isPresent()) {
                outraImagem.get().setPrincipal(true);
            }
        }

        // Excluir o arquivo
        armazenamentoService.excluirArquivo(imagem.getCaminho());

        // Remover a imagem do produto
        produto.removerImagem(imagem);
        produtoRepository.save(produto);
    }

    @Transactional
    public List<ImagemProduto> listarImagensProduto(Long produtoId) {
        Produto produto = buscarPorId(produtoId);
        return new ArrayList<>(produto.getImagens());
    }

    private void validarProduto(ProdutoDTO produtoDTO) {
        if (produtoRepository.existsByCodigo(produtoDTO.getCodigo())) {
            throw new ValidacaoException("Já existe um produto com o código: " + produtoDTO.getCodigo());
        }
    }

    /**
     * Popula o objeto Produto com os dados do ProdutoDTO.
     * Se o usuário autenticado for um estoquista, apenas a quantidade em estoque será atualizada.
     */
    private void popularProdutoComDTO(Produto produto, ProdutoDTO produtoDTO) {
        // Obter a autenticação atual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isEstoquista = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ESTOQUISTA"));

        if (isEstoquista) {
            // O estoquista só pode alterar a quantidade em estoque
            produto.setQuantidadeEstoque(produtoDTO.getQuantidadeEstoque());
        } else {
            produto.setCodigo(produtoDTO.getCodigo());
            produto.setNome(produtoDTO.getNome());
            produto.setDescricao(produtoDTO.getDescricao());
            produto.setPreco(produtoDTO.getPreco());
            produto.setQuantidadeEstoque(produtoDTO.getQuantidadeEstoque());
            produto.setAvaliacao(produtoDTO.getAvaliacao());
            produto.setAtivo(produtoDTO.isAtivo());
        }
    }
}
