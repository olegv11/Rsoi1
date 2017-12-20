package ru.oleg.rsoi.service.mainsite.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.oleg.rsoi.remoteservice.RemoteGatewayService;
import ru.oleg.rsoi.service.mainsite.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationEnricherInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RemoteGatewayService gatewayService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object object, ModelAndView model) throws Exception {
        boolean isAuthorized = Util.checkAuthorizationAndTryRefresh(request, response, gatewayService);
        model.addObject("isAuthenticated", isAuthorized);
    }
}
