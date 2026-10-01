package br.gov.sp.etec.estacionamento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class ConsultaClockConfiguration {
    @Bean
    Clock consultaClock() {
        return Clock.systemDefaultZone();
    }
}
