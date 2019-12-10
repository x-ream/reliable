package io.xream.reliable.remote;

import io.xream.x7.reyc.ReyClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ReyClient("http://${reliable.dashboard.authorization.url.server}" )
public interface AuthorizationServiceRemote {

    @RequestMapping(value = "/{token}/{userId}", method = RequestMethod.GET)
    boolean verify(String token, String userId);
}
