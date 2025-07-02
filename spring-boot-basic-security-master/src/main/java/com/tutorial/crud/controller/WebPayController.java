package com.tutorial.crud.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tutorial.crud.persistence.entity.TransbankProperties;

import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.webpayplus.WebpayPlus.Transaction;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;



@Controller
@RequestMapping("/webpay")
public class WebPayController {

    // Objeto que define las propiedades para la integración de Transbank
    @Autowired
    private TransbankProperties transbankProperties;

    // Esta URL es la del ambiente de prueba local que consulta esta API
    static final String LOCAL_ADDRESS = "https://webpay3gint.transbank.cl";
    // Nombre de la key del token que se envía como parámetro en la URL
    static final String TOKEN_KEY = "token_ws";
    // Configuración de Webpay
    private WebpayOptions webpayOptions;

    @javax.annotation.PostConstruct
    public void init() {
        webpayOptions = new WebpayOptions(
                transbankProperties.getCommerceCode(),
                transbankProperties.getApiKey(),
                IntegrationType.TEST);
    }

        @GetMapping("")
    public String showWebpay() {
        return "producto/webpay";
    }
    @CrossOrigin(origins = LOCAL_ADDRESS)
@PostMapping("/create")
public String createTransaction(
        @org.springframework.web.bind.annotation.RequestParam("buyOrder") String buyOrder,
        @org.springframework.web.bind.annotation.RequestParam("sessionId") String sessionId,
        @org.springframework.web.bind.annotation.RequestParam("amount") double amount,
        org.springframework.ui.Model model) {
    try {
        String returnUrl = "http://localhost:8080/webpay/return"; // Cambia esto según tu URL de retorno

        Transaction transaction = new Transaction(webpayOptions);
// ...existing code...

// ...existing code...
WebpayPlusTransactionCreateResponse response = transaction.create(
    buyOrder,
    sessionId,
    amount,
    returnUrl);
// ...existing code...
// ...existing code...

        // Pasa el token y la URL a la vista
        model.addAttribute("token", response.getToken());
        model.addAttribute("url", response.getUrl());

        return "producto/webpay";
    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", e.getMessage());
        return "producto/webpay";
    }
}

@GetMapping("/return")
public String mostrarcompraexitosa() {
    return  "producto/compra"; // Nombre del HTML de confirmación
}


}
// 