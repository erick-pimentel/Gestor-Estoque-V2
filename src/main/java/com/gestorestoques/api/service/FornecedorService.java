package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.FornecedorDTO;
import com.gestorestoques.api.dto.FornecedorResponseDTO;
import com.gestorestoques.api.model.Fornecedor;
import com.gestorestoques.api.repository.FornecedorRepository;
import com.gestorestoques.api.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class FornecedorService {

    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final FornecedorRepository fornecedorRepository;
    private final ProdutoRepository produtoRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository, ProdutoRepository produtoRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> listarTodos() {
        return fornecedorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponseDTO buscarPorId(Integer id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor nao encontrado"));
        return toResponse(fornecedor);
    }

    @Transactional
    public FornecedorResponseDTO criar(FornecedorDTO dto) {
        validar(dto, null);

        Fornecedor fornecedor = new Fornecedor();
        preencherEntidade(dto, fornecedor);

        return toResponse(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public FornecedorResponseDTO atualizar(Integer id, FornecedorDTO dto) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor nao encontrado"));

        validar(dto, id);
        preencherEntidade(dto, fornecedor);

        return toResponse(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public void deletar(Integer id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor nao encontrado"));

        if (produtoRepository.existsByFornecedorIdFornecedor(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Fornecedor possui produtos vinculados");
        }

        fornecedorRepository.delete(fornecedor);
    }

    private void validar(FornecedorDTO dto, Integer idAtualizacao) {
        if (dto == null || dto.getNome() == null || dto.getNome().isBlank() || dto.getCnpj() == null || dto.getCnpj().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome e CNPJ sao obrigatorios");
        }

        String nomeNormalizado = dto.getNome().trim();
        String cnpjNormalizado = dto.getCnpj().trim();
        if (nomeNormalizado.length() < 2 || nomeNormalizado.length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do fornecedor deve ter entre 2 e 120 caracteres");
        }

        if (!CNPJ_PATTERN.matcher(cnpjNormalizado).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de CNPJ invalido. Use XX.XXX.XXX/XXXX-XX");
        }

        boolean cnpjDuplicado = idAtualizacao == null
                ? fornecedorRepository.existsByCnpj(cnpjNormalizado)
                : fornecedorRepository.existsByCnpjAndIdFornecedorNot(cnpjNormalizado, idAtualizacao);
        if (cnpjDuplicado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ ja cadastrado");
        }

        if (dto.getContato() != null && dto.getContato().trim().length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contato deve ter no maximo 120 caracteres");
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !EMAIL_PATTERN.matcher(dto.getEmail().trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email invalido");
        }
    }

    private void preencherEntidade(FornecedorDTO dto, Fornecedor fornecedor) {
        fornecedor.setNomeFornecedor(dto.getNome().trim());
        fornecedor.setCnpj(dto.getCnpj().trim());
        fornecedor.setContato(dto.getContato() == null || dto.getContato().isBlank() ? null : dto.getContato().trim());
        fornecedor.setEmail(dto.getEmail() == null || dto.getEmail().isBlank() ? null : dto.getEmail().trim());
    }

    private FornecedorResponseDTO toResponse(Fornecedor fornecedor) {
        FornecedorResponseDTO dto = new FornecedorResponseDTO();
        dto.setId(fornecedor.getIdFornecedor());
        dto.setNome(fornecedor.getNomeFornecedor());
        dto.setCnpj(fornecedor.getCnpj());
        dto.setContato(fornecedor.getContato());
        dto.setEmail(fornecedor.getEmail());
        dto.setDataCadastro(fornecedor.getDataCadastro());
        return dto;
    }
}

