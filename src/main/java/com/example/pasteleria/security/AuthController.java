package com.example.pasteleria.security;

import com.example.pasteleria.dto.LoginRequest;
import com.example.pasteleria.model.Usuario;
import com.example.pasteleria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuario o contraseña incorrectos"));
        }


        if (usuario.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuario o contraseña incorrectos"));
        }

        boolean passwordOk = passwordEncoder.matches(
                request.getPassword(),
                usuario.getPassword()
        );

        if (!passwordOk) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuario o contraseña incorrectos"));
        }

        String rol = usuario.getTipoUsuario();
        if (rol == null || rol.isBlank()) rol = "USER";

        String token = jwtUtil.generarToken(usuario.getCorreo(), rol);

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("usuario", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "correo", usuario.getCorreo(),
                "tipoUsuario", rol
        ));

        return ResponseEntity.ok(body);
    }
}
