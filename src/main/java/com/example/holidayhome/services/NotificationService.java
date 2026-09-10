package com.example.holidayhome.services;

import com.example.holidayhome.entities.DigitalKey;
import com.example.holidayhome.entities.Guest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Integrazione con l'API di terze parti Brevo per l'invio delle email transazionali
 * contenenti i codici delle chiavi digitali. La risposta dell'API (messageId) viene
 * restituita al chiamante che la salva su DigitalKey.notificationReference: e' cosi'
 * che l'informazione recuperata dall'API esterna entra a far parte della logica interna
 * (tracciabilita' dell'invio, possibilita' di re-invio, audit).
 * <p>
 * Se la chiamata fallisce (chiave non configurata, servizio non raggiungibile, ecc.)
 * il check-in NON viene bloccato: l'errore viene loggato e il riferimento resta vuoto,
 * cosi' la demo/valutazione del progetto non dipende dalla disponibilita' di un vero
 * account Brevo.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final RestClient brevoRestClient;
    private final String apiKey;
    private final String senderEmail;
    private final String senderName;

    public NotificationService(RestClient brevoRestClient,
                                @Value("${brevo.apiKey}") String apiKey,
                                @Value("${brevo.senderEmail}") String senderEmail,
                                @Value("${brevo.senderName}") String senderName) {
        this.brevoRestClient = brevoRestClient;
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public String sendDigitalKeysEmail(Guest guest, String apartmentCode, List<DigitalKey> keys) {
        try {
            String keysHtml = keys.stream()
                    .map(k -> "<li><b>" + describe(k) + "</b>: codice <code>" + k.getCode() + "</code> - valido dal "
                            + k.getValidFrom() + " al " + k.getValidTo() + "</li>")
                    .collect(Collectors.joining());

            String html = "<p>Gentile " + guest.getFirstName() + " " + guest.getLastName() + ",</p>"
                    + "<p>ecco le chiavi digitali per il tuo soggiorno nell'appartamento " + apartmentCode + ":</p>"
                    + "<ul>" + keysHtml + "</ul>"
                    + "<p>Buon soggiorno!</p>";

            Map<String, Object> payload = Map.of(
                    "sender", Map.of("name", senderName, "email", senderEmail),
                    "to", List.of(Map.of("email", guest.getEmail(), "name", guest.getFirstName() + " " + guest.getLastName())),
                    "subject", "Le tue chiavi digitali - Appartamento " + apartmentCode,
                    "htmlContent", html
            );

            Map<?, ?> response = brevoRestClient.post()
                    .uri("/smtp/email")
                    .header("api-key", apiKey)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            Object messageId = response != null ? response.get("messageId") : null;
            log.info("Email chiave digitale inviata a {} (messageId={})", guest.getEmail(), messageId);
            return messageId != null ? messageId.toString() : null;

        } catch (Exception ex) {
            log.warn("Impossibile inviare l'email con la chiave digitale a {}: {}", guest.getEmail(), ex.getMessage());
            return null;
        }
    }

    private String describe(DigitalKey key) {
        return key.getDoor().getClass().getSimpleName().equals("MainEntranceDoor") ? "Portone principale" : "Porta appartamento";
    }
}
