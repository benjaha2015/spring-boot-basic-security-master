package com.tutorial.crud.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tutorial.crud.persistence.entity.TransbankProperties;
import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.exception.TransactionCreateException;
import cl.transbank.webpay.webpayplus.WebpayPlus.Transaction;


@Controller
public class WebPayController {

    @Autowired
    private TransbankProperties transbankProperties;

    static final String LOCAL_ADDRESS = "https://webpay3gint.transbank.cl";
        static final String TOKEN_KEY = "token_ws";
    private WebpayOptions webpayOptions;

    @PostConstruct
    public void init() {
        webpayOptions = new WebpayOptions(
                transbankProperties.getCommerceCode(),
                transbankProperties.getApiKey(),
                IntegrationType.TEST);
    }

    @GetMapping("/webpay")
    public String showPaymentForm(Model model) {
        model.addAttribute("amount", 0.0); // Inicializar el monto
        return "paymentForm"; // Nombre de la plantilla Thymeleaf
    }

    @PostMapping("/webpay")
    public String createTransaction(@RequestParam("amount") double amount, Model model) {
        try {
            // Generar valores automáticamente
            String buyOrder = String.valueOf(System.currentTimeMillis()); // Ejemplo: usar timestamp como buyOrder
            String sessionId = String.valueOf(System.nanoTime()); // Ejemplo: usar nanoTime como sessionId
            String returnUrl = "http://localhost:8080/return"; // URL de retorno fija

            Transaction transaction = new Transaction(webpayOptions);
            // Crear la transacción y obtener la respuesta
            var response = transaction.create(buyOrder, sessionId, amount, returnUrl);


            // Obtener el token de la respuesta
            String token = response.getToken(); // Asegúrate de que el método getToken() exista

            // Redirigir al usuario a la URL de Webpay con el token
            return "redirect:" + LOCAL_ADDRESS + "/webpay/transaction/" + token; // Redirigir a Webpay
        } catch (TransactionCreateException e) {
            model.addAttribute("error", e.getMessage());
            return "paymentError"; // Nombre de la plantilla Thymeleaf para error
        }
    }

    @GetMapping("/return")
    public String handleReturn(@RequestParam("token_ws") String token, Model model) {
        // Aquí puedes manejar la respuesta de Webpay
        // Implementa la lógica para verificar el estado de la transacción
        return "paymentResult"; // Nombre de la plantilla Thymeleaf para mostrar el resultado
    }
}
