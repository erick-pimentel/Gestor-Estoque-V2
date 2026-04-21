package com.gestorestoques.api.controller;

import com.gestorestoques.api.dto.MovimentacaoDTO;
import com.gestorestoques.api.dto.MovimentacaoResponseDTO;
import com.gestorestoques.api.service.MovimentacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoResponseDTO>> listar() {
        return ResponseEntity.ok(movimentacaoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(movimentacaoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MovimentacaoResponseDTO> criar(@RequestBody MovimentacaoDTO dto) {
        MovimentacaoResponseDTO response = movimentacaoService.criar(dto);
        return ResponseEntity.created(URI.create("/api/movimentacoes/" + response.getId())).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        movimentacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

