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

import com.sun.appserv.security.AppservRealm;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;
import java.security.Principal;
import java.util.Enumeration;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

/**
 * <p>
 * Realm is supporting LoginModule by delegating authentication and
 * authorization to a local bean.</p>
 *
 * <p>
 * Realm and LoginModule are linked in the container configuration (login.conf
 * for Glassfish) :</p>
 * <pre>{@code AuxeanneRealm {
 *   com.auxeanne.realm.LoginModule required;<
 * };}</pre>
 *
 * <p>
 * Listed properties are intented for realm configuration.</p>
 *
 * @author Jean-Michel Tanguy
 */
public class Realm extends AppservRealm {

    /**
     * JAAS_CONTEXT : property used in the login.conf to link the LoginModule
     * and the realm (default : AuxeanneRealm)
     */
    public static final String JAAS_CONTEXT_PROPERTY = "JAAS_CONTEXT";
    /**
     * REALM_NAME : name and description of the type of authentication supported
     * (default : Auxeanne)
     */
    public static final String REALM_NAME_PROPERTY = "REALM_NAME";
    /**
     * LOCAL_BEAN_JNDI : name space of the delegate local bean implementing
     * RealmDelegate (default : java:global/AuxeanneRealmDB/DelegateBean)
     */
    public static final String LOCAL_BEAN_JNDI_PROPERTY = "LOCAL_BEAN_JNDI";

    /**
     * AuxeanneRealm
     */
    private static final String JAAS_CONTEXT_DEFAULT = "AuxeanneRealm";
    /**
     * Auxeanne
     */
    private static final String REALM_NAME_DEFAULT = "Auxeanne";
    /**
     * java:global/AuxeanneRealmDelegate/DB (update required)
     */
    private static final String LOCAL_BEAN_JNDI_DEFAULT = "java:global/AuxeanneRealmDB/DelegateBean";

    /**
     * local bean delegate matching the JNDI
     */
    RealmDelegate localBean;

    /**
     * Properties provided by the realm configuration. They are overriding
     * default property values.
     */
    Properties properties;

    /**
     * Reserved. Called by the container.
     *
     * @param properties from the realm configuration overriding default
     * property values
     * @throws BadRealmException
     * @throws NoSuchRealmException
     */
    @Override
    public void init(Properties properties) throws BadRealmException, NoSuchRealmException {
        super.init(properties);
        this.properties = properties;
    }

    /**
     * JAAS Context used to link the LoginModule and the Realm in the container
     * configuration. Set by the JAAS_CONTEXT_PROPERTY and used in the
     * login.conf in Glassfish.
     *
     * @return JAAS Context name
     */
    @Override
    public synchronized String getJAASContext() {
        return properties.getProperty(JAAS_CONTEXT_PROPERTY, JAAS_CONTEXT_DEFAULT);
    }

    /**
     * Name and description of the realm. Set by the REALM_NAME_PROPERTY
     *
     * @return name of the realm
     */
    @Override
    public String getAuthType() {
        return properties.getProperty(REALM_NAME_PROPERTY, REALM_NAME_DEFAULT);
    }

    /**
     * delegating authentication to local bean
     *
     * @param username
     * @param password
     * @return identification to add to principal
     * @throws LoginException
     */
    public Principal athenticate(String username, String password) throws LoginException {
        try {
            return getLocalBean().authenticate(properties, username, password);
        } catch (javax.ejb.EJBException ex) {
            // allowing local bean hot deploy by capturing JNDI exception
            // forcing new JNDI lookup
            localBean = null;
            // new request
            return getLocalBean().authenticate(properties, username, password);
        }
    }

    /**
     * delegating authorization to local bean
     *
     * @param username
     * @return user's groups
     * @throws InvalidOperationException
     * @throws NoSuchUserException
     */
    @Override
    public Enumeration getGroupNames(String username) throws InvalidOperationException, NoSuchUserException {
        try {
            return getLocalBean().getGroupNames(properties, username);
        } catch (javax.ejb.EJBException ex) {
            // allowing local bean hot deploy by capturing JNDI exception
            // forcing new JNDI lookup
            localBean = null;
            // new request
            try {
                return getLocalBean().getGroupNames(properties, username);
            } catch (NoSuchUserException | InvalidOperationException exception) {
                throw exception;
            } catch (Exception other) {
                throw new InvalidOperationException("");
            }
        } catch (NoSuchUserException | InvalidOperationException exception) {
            throw exception;
        } catch (Exception other) {
            throw new InvalidOperationException("");
        }
    }

    /**
     * retrieving local bean as set by property LOCAL_BEAN_JNDI
     *
     * @return local bean
     * @throws LoginException
     */
    protected RealmDelegate getLocalBean() throws LoginException {
        //  RealmDelegate localBean = null;
        if (localBean == null) {
            String jndi = properties.getProperty(LOCAL_BEAN_JNDI_PROPERTY, LOCAL_BEAN_JNDI_DEFAULT);
            try {
                Context c = new InitialContext();
                localBean = (RealmDelegate) c.lookup(jndi);
            } catch (NamingException ex) {
                Logger.getLogger(Realm.class.getName()).log(Level.SEVERE, null, ex);
                throw new LoginException();
            }
        }
        return localBean;
    }

}
