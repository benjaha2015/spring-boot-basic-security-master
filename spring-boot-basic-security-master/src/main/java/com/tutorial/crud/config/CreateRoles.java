package com.tutorial.crud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.tutorial.crud.service.RolService;
import com.tutorial.crud.entity.Rol;
import com.tutorial.crud.enums.RolNombre;
@Service
public class CreateRoles implements CommandLineRunner {

    @Autowired
    RolService rolService;

    @Override
    public void run(String... args) throws Exception {
        // Rol rolAdmin = new Rol(RolNombre.ROLE_ADMIN);
        // Rol rolUser = new Rol(RolNombre.ROLE_USER);
        // rolService.save(rolAdmin);
        // rolService.save(rolUser);
}
}
