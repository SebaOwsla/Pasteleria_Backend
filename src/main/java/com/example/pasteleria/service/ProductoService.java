package com.example.pasteleria.service;

import com.example.pasteleria.model.Producto;
import com.example.pasteleria.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto update(Long id, Producto datos) {
        return productoRepository.findById(id).map(p -> {
            p.setNombre(datos.getNombre());
            p.setDescripcion(datos.getDescripcion());
            p.setPrecio(datos.getPrecio());
            p.setImagen(datos.getImagen());
            p.setCategoria(datos.getCategoria());
            return productoRepository.save(p);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        return productoRepository.findById(id).map(p -> {
            productoRepository.delete(p);
            return true;
        }).orElse(false);
    }
}
