package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.MovimentacaoDTO;
import com.gestorestoques.api.dto.MovimentacaoResponseDTO;
import com.gestorestoques.api.model.*;
import com.gestorestoques.api.repository.FornecedorRepository;
import com.gestorestoques.api.repository.MovimentacaoRepository;
import com.gestorestoques.api.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MovimentacaoService {

    private static final Pattern AJUSTE_META_PATTERN = Pattern.compile("\\[ESTOQUE_ANTERIOR=(\\d+)]");

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
                               ProdutoRepository produtoRepository,
                               FornecedorRepository fornecedorRepository,
                               UsuarioAutenticadoService usuarioAutenticadoService) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarTodos() {
        return movimentacaoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MovimentacaoResponseDTO buscarPorId(Integer id) {
        Movimentacao movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimentacao nao encontrada"));
        return toResponse(movimentacao);
    }

    @Transactional
    public MovimentacaoResponseDTO criar(MovimentacaoDTO dto) {
        validar(dto);

        Usuario usuario = usuarioAutenticadoService.getUsuarioLogado();
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario nao autenticado");
        }

        Produto produto = produtoRepository.findById(dto.getIdProduto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado"));

        Integer estoqueAnterior = produto.getQuantidade();

        switch (dto.getTipo()) {
            case ENTRADA -> produto.setQuantidade(produto.getQuantidade() + dto.getQuantidade());
            case SAIDA -> {
                if (produto.getQuantidade() < dto.getQuantidade()) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Estoque insuficiente para saida");
                }
                produto.setQuantidade(produto.getQuantidade() - dto.getQuantidade());
            }
            case AJUSTE -> produto.setQuantidade(dto.getQuantidade());
        }

        produtoRepository.save(produto);

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setTipoMovimentacao(dto.getTipo());
        movimentacao.setQuantidadeMovimentacao(dto.getQuantidade());
        movimentacao.setQuantidade(dto.getQuantidade());
        movimentacao.setValorUnitario(dto.getValorUnitario() == null ? BigDecimal.ZERO : dto.getValorUnitario());

        if (dto.getIdFornecedor() != null && dto.getIdFornecedor() > 0) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getIdFornecedor())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor nao encontrado"));
            movimentacao.setFornecedor(fornecedor);
        }

        String observacao = dto.getObservacao();
        if (dto.getTipo() == TipoMovimentacao.AJUSTE) {
            String prefixo = "[ESTOQUE_ANTERIOR=" + estoqueAnterior + "]";
            observacao = observacao == null || observacao.isBlank() ? prefixo : prefixo + " " + observacao;
        }
        movimentacao.setObservacao(observacao);

        return toResponse(movimentacaoRepository.save(movimentacao));
    }

    @Transactional
    public void deletar(Integer id) {
        Movimentacao movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimentacao nao encontrada"));

        Produto produto = movimentacao.getProduto();
        int quantidade = movimentacao.getQuantidadeMovimentacao();

        switch (movimentacao.getTipoMovimentacao()) {
            case ENTRADA -> {
                if (produto.getQuantidade() < quantidade) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Nao foi possivel estornar a entrada. Estoque atual menor que a quantidade da movimentacao");
                }
                produto.setQuantidade(produto.getQuantidade() - quantidade);
            }
            case SAIDA -> produto.setQuantidade(produto.getQuantidade() + quantidade);
            case AJUSTE -> {
                Integer estoqueAnterior = extrairEstoqueAnterior(movimentacao.getObservacao());
                if (estoqueAnterior == null) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Nao foi possivel estornar o ajuste sem metadado de estoque anterior");
                }
                produto.setQuantidade(estoqueAnterior);
            }
        }

        produtoRepository.save(produto);
        movimentacaoRepository.delete(movimentacao);
    }

    private void validar(MovimentacaoDTO dto) {
        if (dto == null || dto.getIdProduto() == null || dto.getTipo() == null || dto.getQuantidade() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idProduto, tipo e quantidade sao obrigatorios");
        }

        if (dto.getQuantidade() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade da movimentacao deve ser maior que zero");
        }
    }

    private Integer extrairEstoqueAnterior(String observacao) {
        if (observacao == null) {
            return null;
        }
        Matcher matcher = AJUSTE_META_PATTERN.matcher(observacao);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private MovimentacaoResponseDTO toResponse(Movimentacao movimentacao) {
        MovimentacaoResponseDTO dto = new MovimentacaoResponseDTO();
        dto.setId(movimentacao.getIdMovimentacao());
        dto.setIdProduto(movimentacao.getProduto().getIdProduto());
        dto.setNomeProduto(movimentacao.getProduto().getNomeProduto());
        dto.setIdUsuario(movimentacao.getUsuario().getIdUsuario());
        dto.setNomeUsuario(movimentacao.getUsuario().getNomeUsuario());
        dto.setTipo(movimentacao.getTipoMovimentacao());
        dto.setQuantidade(movimentacao.getQuantidadeMovimentacao());
        dto.setValorUnitario(movimentacao.getValorUnitario());
        dto.setDataMovimentacao(movimentacao.getDataMovimentacao());
        dto.setObservacao(movimentacao.getObservacao());

        if (movimentacao.getFornecedor() != null) {
            dto.setIdFornecedor(movimentacao.getFornecedor().getIdFornecedor());
            dto.setNomeFornecedor(movimentacao.getFornecedor().getNomeFornecedor());
        }

        return dto;
    }
}

