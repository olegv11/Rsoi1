package ru.oleg.rsoi.service.mainsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import ru.oleg.rsoi.remoteservice.RemoteGatewayService;
import ru.oleg.rsoi.remoteservice.TokenPair;
import ru.oleg.rsoi.service.mainsite.util.Util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Controller
public class IndexController {
    @Autowired
    RemoteGatewayService gatewayService;


    @RequestMapping("/")
    String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        boolean isAuthenticated = Util.checkAuthorizationAndTryRefresh(request, response, gatewayService);
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "index";
    }

    @RequestMapping("/authorized")
    String authorized(@RequestParam(name = "code") String code, HttpServletResponse servletResponse) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic Z2F0ZXdheUNsaWVudDpnYXRld2F5U2VjcmV0Cg");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", "http://localhost:8080/authorized");
        map.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<HashMap> response = rt.postForEntity("http://localhost:8093/oauth/token",
                request, HashMap.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            return null;
        }

        TokenPair result = new TokenPair();
        result.setAccessToken((String)response.getBody().get("access_token"));
        result.setRefreshToken((String)response.getBody().get("refresh_token"));

        Util.setTokenCookies(result.getAccessToken(), result.getRefreshToken(), servletResponse);

        return "redirect:/";
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Util.clearTokenCookies(response);
        return "redirect:/";
    }

    @RequestMapping(value = "/forbidden")
    public String forbidden() {
        return "forbidden";
    }
}
