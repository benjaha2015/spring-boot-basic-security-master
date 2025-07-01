package com.tutorial.crud.config;

import com.tutorial.crud.entity.Rol;
import com.tutorial.crud.entity.Usuario;
import com.tutorial.crud.enums.RolNombre;
import com.tutorial.crud.service.RolService;
import com.tutorial.crud.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CreateAdmin implements CommandLineRunner {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RolService rolService;

    @Override
    public void run(String... args) throws Exception {
        //  Se añade una comprobación para evitar errores de duplicados
        if (!usuarioService.existsByNombreusuario("admin")) {
            Usuario usuario = new Usuario();
            usuario.setNombre("Admin"); // It's good to assign a visible name
            usuario.setNombreUsuario("admin");
            usuario.setEmail("admin@tutorial.com"); // Y un email
            usuario.setPassword(passwordEncoder.encode("admin"));

            Rol rolAdmin = rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get();
            Rol rolUser = rolService.getByRolNombre(RolNombre.ROLE_USER).get();

            Set<Rol> roles = new HashSet<>();
            roles.add(rolAdmin);
            roles.add(rolUser);
            usuario.setRoles(roles);
            
            usuarioService.save(usuario);
        }
    }
}