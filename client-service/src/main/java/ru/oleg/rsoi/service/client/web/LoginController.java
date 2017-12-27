package ru.oleg.rsoi.service.client.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.oleg.rsoi.service.client.domain.Client;
import ru.oleg.rsoi.service.client.domain.Role;
import ru.oleg.rsoi.service.client.repository.ClientRepository;

@Controller
public class LoginController {
        @GetMapping("/login")
    public String login() {
        return "login";
    }
}
