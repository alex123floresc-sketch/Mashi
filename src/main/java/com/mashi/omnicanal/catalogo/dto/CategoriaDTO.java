package com.mashi.omnicanal.catalogo.dto;

import com.mashi.omnicanal.catalogo.entity.Categoria;

public record CategoriaDTO(
        Long id,
        String nombre,
        String descripcion
) {
    public static CategoriaDTO from(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNombre(), categoria.getDescripcion());
    }
}
