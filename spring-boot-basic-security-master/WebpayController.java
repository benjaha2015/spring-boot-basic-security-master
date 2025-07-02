package com.tutorial.crud.controller;

import java.util.HashMap; 
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.tutorial.crud.dto.TransaccionDTO;

import cl.transbank.common.IntegrationApiKeys;
import cl.transbank.common.IntegrationCommerceCodes;
import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;

@RestController
@RequestMapping("/webpay")
public class WebpayController {

    private static final Logger LOGGER = Logger.getLogger(WebpayController.class.getName());

    // Inyectamos las propiedades de application.properties para ver qué valores tiene
    @Value("${transbank.webpay.plus.commerce-code}")
    private String propsCommerceCode; 

    @Value("${transbank.webpay.plus.api-key}")
    private String propsApiKey; 

    @Value("${transbank.environment}")
    private String propsEnvironment; 

    @Value("${webpay.return-url}")
    private String configuredReturnUrl;

    @Value("${webpay.final-url}")
    private String finalUrl;

    private WebpayPlus.Transaction transaction; 

    @PostConstruct
    public void init() {
        LOGGER.info("Inicializando WebpayController...");
        LOGGER.info("----------------------------------------------------------------------");
        LOGGER.info("Usando credenciales de prueba DIRECTAS DESDE EL SDK para esta prueba.");
        LOGGER.info("Los valores de commerce-code, api-key y environment de application.properties SERÁN IGNORADOS para la configuración de WebpayOptions.");
        LOGGER.info("----------------------------------------------------------------------");

        // Usamos las constantes directamente del SDK de Transbank
        
        String sdkCommerceCode = IntegrationCommerceCodes.WEBPAY_PLUS;
        String sdkApiKey = IntegrationApiKeys.WEBPAY; // Esta es la API Key '579B...'
        IntegrationType sdkIntegrationType = IntegrationType.TEST;

        LOGGER.info("SDK Commerce Code (a usar): " + sdkCommerceCode); 
        LOGGER.info("SDK API Key (a usar): " + sdkApiKey);       
        LOGGER.info("SDK Integration Type (a usar): " + sdkIntegrationType); 

        WebpayOptions options = new WebpayOptions(sdkCommerceCode, sdkApiKey, sdkIntegrationType);
        this.transaction = new WebpayPlus.Transaction(options);
        
        LOGGER.info("SDK de Transbank configurado usando constantes directas del SDK para el ambiente: " + sdkIntegrationType);
        
        // Logueamos los valores de application.properties para ver qué tienen
        LOGGER.info("Valores actuales en application.properties (informativo, NO usados para WebpayOptions en este init):");
        LOGGER.info("   props.commerceCode: " + this.propsCommerceCode);
        LOGGER.info("   props.apiKey: " + this.propsApiKey);
        LOGGER.info("   props.environment: " + this.propsEnvironment);
        LOGGER.info("----------------------------------------------------------------------");
    }

        @GetMapping("")
    public String showWebpay() {
        return "producto/webpay";
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(@RequestBody TransaccionDTO transaccionDTO) {
        String buyOrder = transaccionDTO.getBuyOrder();
        String sessionId = transaccionDTO.getSessionId(); 
        double amount = transaccionDTO.getAmount();

        LOGGER.info(String.format("Intentando crear transacción Webpay: Orden de Compra=%s, SessionId=%s, Monto=%.2f, ReturnUrl=%s",
                buyOrder, sessionId, amount, configuredReturnUrl));

        try {
            final WebpayPlusTransactionCreateResponse response = transaction.create(buyOrder, sessionId, amount, configuredReturnUrl);
            
            Map<String, String> result = new HashMap<>();
            result.put("token", response.getToken());
            result.put("url", response.getUrl()); 

            LOGGER.info("Transacción Webpay creada exitosamente. Token: " + response.getToken() + ", URL: " + response.getUrl());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear la transacción en Webpay Plus", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al crear la transacción: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/return", method = {RequestMethod.GET, RequestMethod.POST})
    public RedirectView handleReturn(@RequestParam(value = "token_ws", required = false) String tokenWs,
                                     @RequestParam(value = "TBK_TOKEN", required = false) String tbkToken,
                                     @RequestParam(value = "TBK_ORDEN_COMPRA", required = false) String tbkBuyOrder,
                                     @RequestParam(value = "TBK_ID_SESION", required = false) String tbkSessionId) {
        
        String token = tokenWs;
        boolean wasAbortedByUser = false;

        LOGGER.info(String.format("Retorno de Webpay recibido. token_ws: %s, TBK_TOKEN: %s, TBK_ORDEN_COMPRA: %s, TBK_ID_SESION: %s",
                tokenWs, tbkToken, tbkBuyOrder, tbkSessionId));

        if (tokenWs == null && tbkToken != null) {
            token = tbkToken; 
            wasAbortedByUser = true;
            LOGGER.warning("Transacción Webpay ANULADA/ABORTADA por el usuario o timeout. TBK_TOKEN: " + token);
            return new RedirectView(finalUrl + "?status=CANCELLED&token=" + token);
        } else if (tokenWs == null && tbkToken == null) {
            LOGGER.severe("Retorno de Webpay SIN TOKEN. No se puede procesar.");
            return new RedirectView(finalUrl + "?status=ERROR&message=NO_TOKEN_RETURNED");
        }
        
        if (wasAbortedByUser) {
             return new RedirectView(finalUrl + "?status=CANCELLED&token=" + token);
        }

        try {
            LOGGER.info("Confirmando transacción Webpay con token_ws: " + token);
            final WebpayPlusTransactionCommitResponse commitResponse = transaction.commit(token);
            LOGGER.info("Respuesta del Commit de Webpay: " + commitResponse.toString());

            if (commitResponse.getResponseCode() == 0) { 
                LOGGER.info("Transacción APROBADA. Orden de Compra: " + commitResponse.getBuyOrder() + 
                            ", Monto: " + commitResponse.getAmount() + 
                            ", Código de Autorización: " + commitResponse.getAuthorizationCode());
                return new RedirectView(finalUrl + String.format("?status=SUCCESS&token=%s&buyOrder=%s&amount=%.2f&authCode=%s",
                        token, commitResponse.getBuyOrder(), commitResponse.getAmount(), commitResponse.getAuthorizationCode()));
            } else {
                LOGGER.warning("Transacción RECHAZADA o FALLIDA. Token: " + token + 
                               ", Status: " + commitResponse.getStatus() +
                               ", ResponseCode: " + commitResponse.getResponseCode());
                return new RedirectView(finalUrl + String.format("?status=FAILED&token=%s&responseCode=%s&statusDetail=%s",
                        token, commitResponse.getResponseCode(), commitResponse.getStatus()));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al confirmar la transacción en Webpay Plus con token: " + token, e);
            return new RedirectView(finalUrl + "?status=ERROR&message=" + e.getMessage().replace(" ", "_"));
        }
    }
}