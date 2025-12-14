package com.example.pasteleria.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String nombre;
    @Column(length = 50)
    private String apellido;
    @Column(unique = true, length = 100)
    private String correo;
    @Column(length = 255)
    private String password;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    @Column(length = 30)
    private String tipoUsuario;
    @Column(length = 20)
    private String telefono;
    @Column(length = 50)
    private String region;
    @Column(length = 50)
    private String comuna;

}

