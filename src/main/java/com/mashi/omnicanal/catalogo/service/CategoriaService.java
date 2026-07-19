package com.mashi.omnicanal.catalogo.service;

import com.mashi.omnicanal.catalogo.dto.CategoriaDTO;
import com.mashi.omnicanal.catalogo.dto.CategoriaRequest;
import com.mashi.omnicanal.catalogo.entity.Categoria;
import com.mashi.omnicanal.catalogo.repository.CategoriaRepository;
import com.mashi.omnicanal.shared.exception.RecursoNoEncontradoException;
import com.mashi.omnicanal.shared.exception.RegistroDuplicadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaDTO> listar() {
        return categoriaRepository.findAll().stream().map(CategoriaDTO::from).toList();
    }

    public CategoriaDTO obtener(Long id) {
        return CategoriaDTO.from(buscarPorId(id));
    }

    @Transactional
    public CategoriaDTO crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RegistroDuplicadoException("Ya existe una categoria con ese nombre");
        }
        Categoria categoria = Categoria.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .build();
        return CategoriaDTO.from(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaDTO actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarPorId(id);
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());
        return CategoriaDTO.from(categoriaRepository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
        categoriaRepository.delete(categoria);
    }

    private Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada: " + id));
    }
}
