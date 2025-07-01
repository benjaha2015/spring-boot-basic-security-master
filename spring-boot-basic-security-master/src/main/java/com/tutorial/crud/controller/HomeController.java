package com.tutorial.crud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
* Este controlador ahora solo maneja las páginas básicas y estáticas de la aplicación.
* Toda la lógica del carrito de compras fue movida a CartController para evitar conflictos y
* mantener el código organizado.
*/
@Controller
@RequestMapping("/")
public class HomeController {

    // === PÁGINAS BÁSICAS ===
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/forbidden")
    public String forbidden() {
        return "forbidden";
    }

   
}