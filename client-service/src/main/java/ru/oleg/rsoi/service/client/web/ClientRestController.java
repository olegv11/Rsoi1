package ru.oleg.rsoi.service.client.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.client.ClientRequest;
import ru.oleg.rsoi.dto.client.ClientResponse;
import ru.oleg.rsoi.service.client.domain.Client;
import ru.oleg.rsoi.service.client.domain.Role;
import ru.oleg.rsoi.service.client.repository.ClientRepository;
import ru.oleg.rsoi.service.client.service.ClientService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;

@RestController
@RequestMapping("/client")
public class ClientRestController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    ClientService clientService;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ClientResponse getClient(@PathVariable Integer id) {
        logger.debug("CLIENT: Getting client " + id);
        return clientService.getById(id).toResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<ClientResponse> getClients(Pageable page) {
        logger.debug("CLIENT: getting page " + page);
        Page<Client> clients = clientService.get(page);
        return clients.map(Client::toResponse);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ClientResponse saveClient(@RequestBody ClientRequest clientRequest,
                                     HttpServletResponse response) {
        logger.debug("CLIENT: creating client " + clientRequest);
        Client client = clientService.save(clientRequest);
        response.addHeader(HttpHeaders.LOCATION, "/client/" + client.getId());
        return client.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteClient(@PathVariable Integer id) {
        logger.debug("CLIENT: deleting client " + id);
        clientService.deleteById(id);
    }

    @Autowired
    @Qualifier("oauthTokenStore")
    TokenStore store;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ConsumerTokenServices consumerTokenServices;

    @RequestMapping(value = "/isAdmin", method = RequestMethod.GET)
    public Boolean isAdmin(@RequestParam(name = "token") String token) {
        OAuth2Authentication auth = store.readAuthentication(token);
        if (auth == null) {
            return false;
        }

        String name = auth.getUserAuthentication().getName();
        Client client = clientRepository.findByUsername(name);
        return client.getRole() == Role.Admin;
    }

    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    UserDetailsService userDetailsService;

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout(@RequestBody String token, HttpServletRequest request) {
        OAuth2RefreshToken refresh = store.readRefreshToken(token);
        if (refresh == null) {
            return;
        }

        OAuth2Authentication auth = store.readAuthenticationForRefreshToken(refresh);
        if (auth == null) {
            return;
        }
        String name = auth.getUserAuthentication().getName();

        Collection<OAuth2AccessToken> accessTokens = store.findTokensByClientIdAndUserName("gatewayClient", name);

        for (OAuth2AccessToken t : accessTokens) {
            consumerTokenServices.revokeToken(t.getValue());
        }

        store.removeAccessTokenUsingRefreshToken(refresh);
        store.removeRefreshToken(refresh);

        UserDetails details = userDetailsService.loadUserByUsername(name);

        for (SessionInformation info : sessionRegistry.getAllSessions(details, false)) {
            info.expireNow();
        }
    }

}
