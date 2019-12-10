package io.xream.reliable.controller;

import io.xream.reliable.remote.AuthorizationServiceRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationBusiness {

    @Value("reliable.dashboard.authorization.required")
    private String authorizationRequired;

    @Value("reliable.dashboard.authorization.url.redirect-to-signIn")
    private String urlRedirectToSignIn = "NONE";

    @Value("reliable.dashboard.authorization.url.server")
    private String urlAuthorization = "NONE";

    @Autowired
    private AuthorizationServiceRemote authorizationServiceRemote;

    public boolean requireAuthorization(){

        return Boolean.parseBoolean(this.authorizationRequired);
    }


    public boolean isAccessble(String token, String userId) {

        if (!requireAuthorization())
            return true;

        return this.authorizationServiceRemote.verify(token, userId);
    }

    public String getRedirect(){
        return "redirect:"+this.urlRedirectToSignIn;
    }
}
