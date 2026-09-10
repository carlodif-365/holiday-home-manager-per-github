package com.example.holidayhome.tools;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Client REST per Brevo (https://developers.brevo.com/reference/sendtransacemail),
 * il servizio di terze parti usato per notificare via email la chiave digitale
 * all'ospite al momento del check-in.
 */
@Configuration
public class BrevoConfig {

    @Bean
    public RestClient brevoRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
