package com.miaueauau.clinica_veterinaria.config;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate5JakartaModule hibernate5Module() {
        // Este módulo ensina Jackson a lidar com proxies do Hibernate e lazy loading.
        // É a forma mais robusta de lidar com serialização de entidades JPA.
        return new Hibernate5JakartaModule();
    }
}