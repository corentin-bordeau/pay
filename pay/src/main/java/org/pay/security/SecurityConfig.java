/**
 * La classe SecurityConfig est responsable de la configuration de la sécurité pour l'application Spring Boot.
 * Elle utilise Spring Security pour définir les paramètres de sécurité au niveau de l'application, y compris
 * la configuration CORS, la désactivation de CSRF, la mise en place de la politique de sécurité HTTP Strict Transport Security (HSTS),
 * la gestion de la session sans état, et l'autorisation des requêtes.
 *
 * Détails des configurations :
 * - CORS : Applique la configuration CORS par défaut pour permettre les requêtes inter-domaines de manière sécurisée.
 * - CSRF : Désactive la protection CSRF, une pratique courante pour les applications basées sur API où les tokens CSRF ne sont pas nécessaires.
 * - HSTS : Configure HTTP Strict Transport Security pour forcer les navigateurs à utiliser uniquement des connexions HTTPS,
 *   ce qui augmente la sécurité en prévenant les attaques de type man-in-the-middle.
 * - Gestion de Session : Définit la politique de création de session à 'STATELESS' pour indiquer à Spring Security
 *   que l'application ne doit pas maintenir l'état de la session entre les requêtes. Cela est typique des applications API RESTful.
 * - Autorisation des Requêtes : Permet toutes les requêtes par défaut. Cette configuration peut être ajustée pour restreindre l'accès
 *   à certaines parties de l'application selon les besoins de sécurité spécifiques.
 *
 * Cette configuration est essentielle pour assurer que l'application communique de manière sécurisée et est protégée contre les attaques courantes.
 */

package org.pay.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Appliquer la configuration CORS par défaut
                .cors(withDefaults())
                // Désactiver CSRF pour les applications basées sur API
                .csrf(csrf -> csrf.disable())
                // Configurer HSTS pour forcer HTTPS
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000) // 1 an
                        )
                )
                // Configurer la gestion de la session pour être sans état
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Autoriser toutes les requêtes par défaut
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
