
package com.lab_prog.streaming.controllers.content;

import com.lab_prog.streaming.dtos.midia.MidiaResponse;
import com.lab_prog.streaming.services.MidiaService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contents")
public class ContentController {

    private final MidiaService midiaService;

    public ContentController(MidiaService midiaService) {
        this.midiaService = midiaService;
    }

    // GET /api/contents
    @GetMapping
    public List<MidiaResponse> listar() {
        return midiaService.listar();
    }

    // GET /api/contents/{id}
    @GetMapping("/{id}")
    public MidiaResponse buscarPorId(@PathVariable UUID id) {
        return midiaService.buscarPorId(id);
    }

    // GET /api/contents/featured
    @GetMapping("/featured")
    public List<MidiaResponse> listarDestaques() {
        return midiaService.listarDestaques();
    }

    // GET /api/contents/trending
    @GetMapping("/trending")
    public List<MidiaResponse> listarEmAlta() {
        return midiaService.listarEmAlta();
    }

    // GET /api/contents/new-releases
    @GetMapping("/new-releases")
    public List<MidiaResponse> listarLancamentos() {
        return midiaService.listarLancamentos();
    }
}