package ru.oleg.rsoi.serviceAuth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


// public RemoteGatewayServiceImpl(@Value("${urls.services.gateway}") String gatewayServiceUrl) {


@Configuration
public class ServiceConfig {

    @Bean
    ServiceCredentials serviceCredentials(@Value("${rsoi.service.appId}") String appId,
                                          @Value("${rsoi.service.appSecret}") String appSecret) {
        ServiceCredentials creds = new ServiceCredentials();
        creds.setAppId(appId);
        creds.setAppSecret(appSecret);
        return creds;
    }

    @Bean(name = "allowedCredentials")
    @Scope("singleton")
    List<ServiceCredentials> allowedCredentials(
            @Value("#{'${rsoi.services.creds}'.split(';')}") List<String> credentials) {
        List<ServiceCredentials> result = new ArrayList<>();

        for (String s: credentials) {
            String[] c = s.split(":");

            ServiceCredentials cred = new ServiceCredentials();
            cred.setAppId(c[0]);
            cred.setAppSecret(c[1]);

            result.add(cred);
        }

        return result;
    }

    @Bean(name = "gatewayTokens")
    @Scope("singleton")
    ServiceTokens gatewayTokens() {
        return new ServiceTokens();
    }

    @Bean(name = "movieTokens")
    @Scope("singleton")
    ServiceTokens movieTokens() {
        return new ServiceTokens();
    }

    @Bean(name = "reservationTokens")
    @Scope("singleton")
    ServiceTokens reservationTokens() {
        return new ServiceTokens();
    }

    @Bean(name = "billingTokens")
    @Scope("singleton")
    ServiceTokens billingTokens() {
        return new ServiceTokens();
    }

    @Bean(name = "paymentTokens")
    @Scope("singleton")
    ServiceTokens paymentTokens() {
        return new ServiceTokens();
    }
}
