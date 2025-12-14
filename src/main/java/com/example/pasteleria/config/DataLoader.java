package com.example.pasteleria.config;

import com.example.pasteleria.model.Producto;
import com.example.pasteleria.model.Usuario;
import com.example.pasteleria.repository.ProductoRepository;
import com.example.pasteleria.repository.UsuarioRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    Faker faker = new Faker();

    @Override
    public void run(String... args) {



        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("Master");
            admin.setCorreo("admin@duoc.cl");
            admin.setPassword(passwordEncoder.encode("Admin123*"));
            admin.setTipoUsuario("ADMIN");
            usuarioRepository.save(admin);

            for (int i = 0; i < 5; i++) {
                Usuario u = new Usuario();
                u.setNombre(faker.name().firstName());
                u.setApellido(faker.name().lastName());
                u.setCorreo("user" + i + "@gmail.com");
                u.setPassword(passwordEncoder.encode("User1234*"));
                u.setTipoUsuario("USER");
                usuarioRepository.save(u);
            }
        }


        if (productoRepository.count() == 0) {
            productoRepository.save(new Producto(
                    "Brownie",
                    "Brownie artesanal delicioso",
                    15000,
                    "https://cdn.recetasderechupete.com/wp-content/uploads/2019/11/Brownie.jpg",
                    "tortaPersonalizada"
            ));

            productoRepository.save(new Producto(
                    "Cheesecake",
                    "Pastel frío de queso crema",
                    18000,
                    "https://www.recetasnestle.cl/sites/default/files/srh_recipes/d1d59ba7da4f07af4c9a8b051faab01f.jpg",
                    "tortaClasica"
            ));

            productoRepository.save(new Producto(
                    "Tres Leches",
                    "Torta húmeda tradicional",
                    14000,
                    "https://recetasdecocina.elmundo.es/wp-content/uploads/2024/05/tarta-de-tres-leches.jpg",
                    "tortaClasica"
            ));

            productoRepository.save(new Producto(
                    "Torta Selva Negra",
                    "Torta húmeda tradicional",
                    15000,
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQHq2maiKFG4svKCsLBgS38zBe48BSFf1JpuA&s",
                    "tortaClasica"
            ));

            productoRepository.save(new Producto(
                    "Torta Circular de Manjar",
                    "Torta tradicional chilena con manjar y nueces, un deleite para los amantes de los sabores dulces y clásicos",
                    15000,
                    "https://cdn0.recetasgratis.net/es/posts/8/0/2/torta_milhojas_24208_orig.jpg",
                    "tortaClasica"
            ));

            productoRepository.save(new Producto(
                    "Mousse de Chocolate",
                    "Postre individual cremoso y suave, hecho con chocolate de alta calidad, ideal para los amantes del chocolate.",
                    5000,
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT3VQUDex5Gn1XNB6Cd1RhrXOKa9MYemAIl3A&s",
                    "postresIndividuales"
            ));
        }
    }
}