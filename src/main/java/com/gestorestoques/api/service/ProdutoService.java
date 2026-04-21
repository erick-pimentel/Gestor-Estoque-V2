package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.ProdutoDTO;
import com.gestorestoques.api.dto.ProdutoResponseDTO;
import com.gestorestoques.api.model.Fornecedor;
import com.gestorestoques.api.model.Produto;
import com.gestorestoques.api.model.Usuario;
import com.gestorestoques.api.repository.FornecedorRepository;
import com.gestorestoques.api.repository.MovimentacaoRepository;
import com.gestorestoques.api.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ProdutoService {

    private static final Pattern CODIGO_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{1,40}$");

    private final ProdutoRepository produtoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ProdutoService(ProdutoRepository produtoRepository,
                          FornecedorRepository fornecedorRepository,
                          MovimentacaoRepository movimentacaoRepository,
                          UsuarioAutenticadoService usuarioAutenticadoService) {
        this.produtoRepository = produtoRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Integer id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado"));
        return toResponse(produto);
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoDTO dto) {
        validar(dto, null);

        Usuario usuarioLogado = usuarioAutenticadoService.getUsuarioLogado();
        Produto produto = new Produto();
        preencherEntidade(dto, produto, usuarioLogado);

        return toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Integer id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado"));

        validar(dto, id);

        Usuario usuarioLogado = usuarioAutenticadoService.getUsuarioLogado();
        preencherEntidade(dto, produto, usuarioLogado);

        return toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(Integer id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado"));

        if (movimentacaoRepository.existsByProdutoIdProduto(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Produto possui movimentacoes vinculadas");
        }

        produtoRepository.delete(produto);
    }

    private void validar(ProdutoDTO dto, Integer idAtualizacao) {
        if (dto == null || dto.getNome() == null || dto.getNome().isBlank() || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome e codigo do produto sao obrigatorios");
        }

        String nomeNormalizado = dto.getNome().trim();
        String codigoNormalizado = dto.getCodigo().trim().toUpperCase();
        if (nomeNormalizado.length() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do produto deve ter ao menos 2 caracteres");
        }
        if (!CODIGO_PATTERN.matcher(codigoNormalizado).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codigo do produto possui formato invalido");
        }

        boolean codigoDuplicado = idAtualizacao == null
                ? produtoRepository.existsByCodigoProduto(codigoNormalizado)
                : produtoRepository.existsByCodigoProdutoAndIdProdutoNot(codigoNormalizado, idAtualizacao);
        if (codigoDuplicado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Codigo do produto ja cadastrado");
        }

        if (dto.getQuantidade() == null || dto.getQuantidade() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade nao pode ser negativa");
        }

        if (dto.getPrecoCusto() == null || dto.getPrecoCusto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preco de custo deve ser maior que zero");
        }

        if (dto.getPrecoVenda() == null || dto.getPrecoVenda().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preco de venda deve ser maior que zero");
        }

        if (dto.getPrecoVenda().compareTo(dto.getPrecoCusto()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preco de venda nao pode ser menor que o preco de custo");
        }

        if (dto.getIdFornecedor() == null || dto.getIdFornecedor() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fornecedor e obrigatorio");
        }

        if (!fornecedorRepository.existsById(dto.getIdFornecedor())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor nao encontrado");
        }
    }

    private void preencherEntidade(ProdutoDTO dto, Produto produto, Usuario usuarioLogado) {
        produto.setNomeProduto(dto.getNome().trim());
        produto.setCodigoProduto(dto.getCodigo().trim().toUpperCase());
        produto.setQuantidade(dto.getQuantidade());
        produto.setPrecoCusto(dto.getPrecoCusto());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setValidade(dto.getValidade());
        produto.setUsuario(usuarioLogado);

        if (dto.getIdFornecedor() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getIdFornecedor())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor nao encontrado"));
            produto.setFornecedor(fornecedor);
        } else {
            produto.setFornecedor(null);
        }
    }

    private ProdutoResponseDTO toResponse(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getIdProduto());
        dto.setNome(produto.getNomeProduto());
        dto.setCodigo(produto.getCodigoProduto());
        dto.setQuantidade(produto.getQuantidade());
        dto.setPrecoCusto(produto.getPrecoCusto());
        dto.setPrecoVenda(produto.getPrecoVenda());
        dto.setValidade(produto.getValidade());

        if (produto.getFornecedor() != null) {
            dto.setIdFornecedor(produto.getFornecedor().getIdFornecedor());
            dto.setNomeFornecedor(produto.getFornecedor().getNomeFornecedor());
        }

        if (produto.getUsuario() != null) {
            dto.setIdUsuario(produto.getUsuario().getIdUsuario());
            dto.setNomeUsuario(produto.getUsuario().getNomeUsuario());
        }

        return dto;
    }
}

