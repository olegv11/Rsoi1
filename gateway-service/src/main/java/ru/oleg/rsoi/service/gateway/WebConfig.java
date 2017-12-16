package ru.oleg.rsoi.service.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.oleg.rsoi.service.gateway.web.AuthInterceptor;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    AuthInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registrty) {
        registrty.addInterceptor(interceptor)
                .addPathPatterns("./movies/", "./movie/**");
    }
}
