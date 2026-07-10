package com.clinica.agenda.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {
    @Bean
    public WebClient pacienteWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8083/api/pacientes")
                .build();

    }

  

}
