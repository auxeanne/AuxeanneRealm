## AuxeanneRealmAPI
AuxeanneRealmAPI targets Glassfish application server to extend easily Java Authentication and Authorization Service (JAAS).

This API delegates these tasks to a local EJB bean deployed in the Glassfish instance (WAR, EAR) allowing:
  - full usage of the container features (JPA, CDI, ...)
  - handling custom login, remember me, multi-tenant, ...
  - hot deployment (no need to restart the server instance)
  - while keeping the benefits of container managed security

## Glassfish Setup

1 - Add this project JAR to the domain "lib" folder

2 - Complete the "login.conf" file in the "config" directory with :

       AuxeanneRealm {
          com.auxeanne.realm.LoginModule required;
       };
       
3 - Create a realm in Configuration > server-config > Security > realms.
  - give any name to the realm (ex : MyRealm) - set the custom class name as "com.auxeanne.realm.Realm"
  - add the property LOCAL_BEAN_JNDI with the JNDI name space of the bean implementing the RealmDelegate interface
      (ex: java:global/MyDelegateApplicationName/MyLocalBean )

## Delegate Application Setup

The authentication and authorization are performed by an EJB bean implementing the RealmDelegate interface. 
Then, AuxeanneRealmAPI is required as dependency.  

Be careful to set the LOCAL_BEAN_JNDI attribute in the realm configuration to the right JNDI name space.

## Client Application Setup

Web.xml just needs to reference the realm and use appropriate groups. The Delegate Application can be merged with the Client Application.

See the AuxeanneRealmDB project for a reference implementation.
