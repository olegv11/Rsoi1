package ru.oleg.rsoi.service.mainsite.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.oleg.rsoi.service.mainsite.security.AuthenticationInterceptor;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    AuthenticationInterceptor interceptor;

    @Autowired
    AuthenticationEnricherInterceptor enricher;

    @Override
    public void addInterceptors(InterceptorRegistry registrty) {
        registrty.addInterceptor(interceptor)
                .excludePathPatterns("/", "/authorized", "/logout", "/movies", "/forbidden");
        registrty.addInterceptor(enricher)
                .addPathPatterns("/", "/authorized", "/logout", "/movies", "/forbidden");
    }
}
