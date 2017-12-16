package ru.oleg.rsoi.service.mainsite.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.oleg.rsoi.remoteservice.RemoteGatewayService;
import ru.oleg.rsoi.service.mainsite.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    RemoteGatewayService gatewayService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {

        if (!Util.checkAuthorizationAndTryRefresh(request, response, gatewayService)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.sendRedirect("/forbidden");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object object, ModelAndView model) throws Exception {
        model.addObject("isAuthenticated", true);
    }

}
