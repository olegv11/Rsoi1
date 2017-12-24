package ru.oleg.rsoi.service.gateway.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.oleg.rsoi.remoteservice.TokenPair;
import ru.oleg.rsoi.serviceAuth.JwtToken;
import ru.oleg.rsoi.serviceAuth.ServiceCredentials;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    @Qualifier(value = "allowedCredentials")
    List<ServiceCredentials> allowedCredentials;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        return true;
    }
}
