package com.example.pasteleria.controller;

import com.example.pasteleria.model.Producto;
import com.example.pasteleria.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/productos")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Producto> create(@RequestBody Producto p) {


        if (p.getPrecio() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Producto guardado = productoRepository.save(p);

        return ResponseEntity
                .created(URI.create("/api/v1/admin/productos/" + guardado.getId()))
                .body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(@PathVariable Long id,
                                           @RequestBody Producto p) {


        if (p.getPrecio() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return productoRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(p.getNombre());
                    existente.setDescripcion(p.getDescripcion());
                    existente.setPrecio(p.getPrecio());
                    existente.setImagen(p.getImagen());
                    existente.setCategoria(p.getCategoria());

                    Producto actualizado = productoRepository.save(existente);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
