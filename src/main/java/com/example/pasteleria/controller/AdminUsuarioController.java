package com.example.pasteleria.controller;

import com.example.pasteleria.model.Usuario;
import com.example.pasteleria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/v1/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            Usuario saved = usuarioRepository.save(usuario);
            saved.setPassword(null);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Correo ya existe o constraint violado"));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Usuario cambios) {
        return usuarioRepository.findById(id).map(u -> {

            u.setNombre(cambios.getNombre());
            u.setApellido(cambios.getApellido());
            u.setCorreo(cambios.getCorreo());
            u.setTipoUsuario(cambios.getTipoUsuario()); // "ADMIN" o "USER"


            if (cambios.getPassword() != null && !cambios.getPassword().isBlank()) {
                u.setPassword(passwordEncoder.encode(cambios.getPassword()));
            }

            Usuario updated = usuarioRepository.save(u);
            updated.setPassword(null);
            return ResponseEntity.ok(updated);

        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body((Usuario) Map.of("error", "Usuario no encontrado")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
