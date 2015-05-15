/**
 * <p>AuxeanneRealmAPI targets Glassfish application server to extend easily 
 * Java Authentication and Authorization Service (JAAS).</p>
 * 
 * <p>This API delegates these tasks to a local EJB bean deployed in the
 * Glassfish instance (WAR, EAR) allowing:</p>
 * <ul>
 * <li>full usage of the container features (JPA, CDI, ...)</li>
 * <li>handling custom login, remember me, multi-tenant, ...</li>
 * <li>hot deployment (no need to restart the server instance)</li>
 * <li>while keeping the benefits of container managed security</li>
 * </ul>
 * 
 * <h2>Glassfish Setup</h2>
 * 
 * <ol>
 * <li>Add this project JAR to the domain "lib" folder</li>
 * <li>Complete the "login.conf" file in the "config" directory with :
 * <pre>{@code AuxeanneRealm {
 *    com.auxeanne.realm.LoginModule required;
 * };}</pre></li>
 * <li>Create a realm in Configuration : server-config : Security : realms.<br>
 * - give any name to the realm (ex : MyRealm)
 * - set the custom class name as "com.auxeanne.realm.Realm"<br>
 * - add the property LOCAL_BEAN_JNDI with the JNDI name space of the bean implementing the RealmDelegate interface
 *  <br> (ex: java:global/MyDelegateApplicationName/MyLocalBean  )<br> 
 * </li>
 * </ol>
 * 
 * <h2>Delegate Application Setup</h2>
 * 
 * <p>The application which is providing the authentication and authorization must have this project as a dependency and provide
 * an EJB bean implementing the RealmDelegate interface.</p>
 * <p>Be careful to set the LOCAL_BEAN_JNDI attribute in the realm configuration to the right JNDI name space.</p>
 * 
 * 
 * <h2>Client Application Setup</h2>
 * 
 * <p>Web.xml just needs to reference the realm and use appropriate groups.
 * The Delegate Application can be merged with the Client Application.</p>
 * 
 * <p>See the AuxeanneRealmDB project for a reference implementation.</p>
 * 
 * @author Jean-Michel Tanguy
 * 
 */
package com.auxeanne.realm;
