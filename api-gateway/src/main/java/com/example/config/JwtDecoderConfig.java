//package com.example.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.jwt.*;
//
//import javax.crypto.spec.SecretKeySpec;
//
//@Configuration
//public class JwtDecoderConfig {
//
//    @Bean
//    public ReactiveJwtDecoder jwtDecoder() {
//        // Use your secret key
//        String secretKey = "your-256-bit-secret";
//
//        return NimbusReactiveJwtDecoder.withSecretKey(
//                new SecretKeySpec(secretKey.getBytes(), "HmacSHA256")
//        ).build();
//    }
////    @Bean
////    public ReactiveJwtDecoder jwtDecoder() {
////        return NimbusReactiveJwtDecoder.withPublicKey(rsaPublicKey).build();
////    }
//
//}
//
