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

import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Properties;
import javax.ejb.Local;
import javax.security.auth.login.LoginException;

/**
 * Targeted local bean must implement this interface to process authentication
 * and authorization. So, it is required to add this JAR has dependency.
 * 
 * @author Jean-Michel Tanguy
 */
@Local
public interface RealmDelegate  {
    
    /**
     * Performing custom authentication (DB, LDAP, File, ...). <br>
     * Custom Principal can be returned and added to the Subject in order to
     * pass additional information or configuration to the client application.
     * Subject is then retrieved and processed as follow :
     * <pre>{@code Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
     *   Set<MyCustomPrincipal> principals = subject.getPrincipals(MyCustomPrincipal.class);
     *   for (MyCustomPrincipal principal : principals) {
     *       // do necessary tasks
     *   } }</pre>
     * 
     * @param properties passing realm properties from container configuration
     * @param username container managed username
     * @param password container managed password
     * @return a custom Principal can be provided to be added to the Subject (or null)
     * @throws LoginException any exception should be return as a LoginException to prevent insights to credential context
     */
    Principal authenticate(Properties properties, String username, String password) throws LoginException;
    
    /**
     * providing groups for username as configured in the web.xml of the client project
     * @param properties passing realm properties from container configuration
     * @param username container managed username
     * @return groups list of groups (or roles) for username
     * @throws InvalidOperationException
     * @throws NoSuchUserException 
     */
    Enumeration getGroupNames(Properties properties, String username)  throws InvalidOperationException, NoSuchUserException ;
    
}
