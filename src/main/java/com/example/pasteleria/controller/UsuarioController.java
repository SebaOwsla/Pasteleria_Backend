package com.example.pasteleria.controller;

import com.example.pasteleria.dto.LoginRequest;
import com.example.pasteleria.model.Usuario;
import com.example.pasteleria.security.JwtUtil;
import com.example.pasteleria.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/v1/usuarios")
@Tag(name = "Usuarios", description = "Operaciones sobre usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Obtiene una lista de todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<List<Usuario>> getAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario", description = "Obtiene un usuario a traves del ID")
    @Parameter(description = "Id del usuario", required = true, name = "id")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado")));
    }

    @PostMapping
    @Operation(summary = "Crea un nuevo usuario", description = "Crea un usuario a traves de un objeto JSON")
    public ResponseEntity<?> save(@RequestBody Usuario usuario) {
        try {
            // ✅ Forzar INSERT siempre
            usuario.setId(null);

            // ✅ Validaciones mínimas (evitan 400 raros)
            if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo es obligatorio"));
            }

            if ("string".equalsIgnoreCase(usuario.getCorreo().trim())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "En Swagger cambie el correo 'string' por un correo real y único"
                ));
            }

            // ✅ Evitar duplicado antes de pegarle a la BD
            Usuario existente = usuarioService.findByCorreo(usuario.getCorreo());
            if (existente != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está registrado"));
            }

            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La contraseña es obligatoria"));
            }

            String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(passwordEncriptada);

            Usuario newUsuario = usuarioService.save(usuario);
            return new ResponseEntity<>(newUsuario, HttpStatus.CREATED);

        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String detalle = ex.getMostSpecificCause() != null
                    ? ex.getMostSpecificCause().getMessage()
                    : ex.getMessage();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Correo ya existe o constraint violado",
                            "detalle", detalle
                    ));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borra un usuario", description = "Borra un usuario a traves del ID")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Usuario usuario = usuarioService.findByCorreo(request.getCorreo());

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Correo no registrado"));
        }

        boolean passwordOk = passwordEncoder.matches(
                request.getPassword(),
                usuario.getPassword()
        );

        if (!passwordOk) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Contraseña incorrecta"));
        }

        String token = jwtUtil.generarToken(
                usuario.getCorreo(),
                usuario.getTipoUsuario()
        );

        usuario.setPassword(null);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "usuario", usuario
        ));
    }
}
