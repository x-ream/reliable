/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xream.reliable.controller;

import io.xream.reliable.remote.AuthorizationServiceRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @Author Sim
 */
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
