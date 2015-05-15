/*
 * Copyright 2015 Jean-Michel Tanguy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.auxeanne.realm;

import com.sun.appserv.security.AppservPasswordLoginModule;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.security.auth.login.LoginException;


/**
 * <p>Implementing a Glassfish specific JAAS authentication module as defined:
 * <a href="https://docs.oracle.com/cd/E19776-01/820-4496/beabs/index.html">Oracle
 * Creating a Custom Realm</a></p>
 * 
 * <p>Note : the login module must extend the abstract class
 * AppservPasswordLoginModule instead of javax.security.auth.spi.LoginModule
 * which is suitable for JASPIC client only.</p>
 * 
 * @author Jean-Michel Tanguy
 */
public class LoginModule extends AppservPasswordLoginModule {

    /**
     * called by the container to perform authentication
     * @throws LoginException authentication failure is propagated by exception
     */
    @Override
    protected void authenticateUser() throws LoginException {
        // supporting only Auxeanne realm
        if (!(_currentRealm instanceof Realm)) {
            throw new LoginException("com.auxeanne.realm.LoginModule is supporting only com.auxeanne.realm.Realm, check container configuation (ex: login.conf for Glassfish)");
        }
        Realm realm = (Realm) _currentRealm;
        // Passing on to the realm for authentication. Exception is thrown if failing.
        String password = new String(getPasswordChar());
        Principal principal = realm.athenticate(_username, password);
        // adding custom principal to subject
        if (principal!=null) {
            _subject.getPrincipals().add(principal);
        }
        // authorizations
        try {
            Enumeration<String> groupEnum = realm.getGroupNames(_username);
            ArrayList<String> groupList = new ArrayList<>();
            while (groupEnum.hasMoreElements()) {
                groupList.add(groupEnum.nextElement());
            }
            // mendatory per specification of the parent abstract class
            commitUserAuthentication(groupList.toArray(new String[groupList.size()]));
        } catch (Exception ex) {
            throw new LoginException();
        }
    }
    
}
