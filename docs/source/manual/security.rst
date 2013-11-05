.. _manual-sec:

===============
Minnal Security
===============
The security module comes up with a couple of pre-defined authenticators and also allows you to extend and create custom authenticators. The authenticator takes in a ``credential``, validates it and returns back a ``principal``. The credential can be username/password or an auth token and the principal will be the user object.

This module uses a session store for storing the auth tokens. Once an user is authenticated, minnal creates a session for the user and a session token will be returned back to the caller as a cookie. The subsequent calls should send the session token in the request cookie for authenticity.

The security is configured at application level. Below is a sample security configuration of an application.

.. literalinclude:: application_config.yml
	:language: yaml
	:linenos:
	:lines: 20-28

Session Store
=============
The session store stores the user sessions. A session is a bunch of key-value attributes and maps the session token with an auth token. The security module comes with an inbuilt ``org.minnal.security.session.JpaSessionStore`` that stores the sessions in a database table.

You can change the sesison store from the security configuration of the application.

.. literalinclude:: application_config.yml
	:language: yaml
	:linenos:
	:lines: 20,26-27

Authenticators
==============
*Authenticators* are the one that actually authenticate the incoming requests based on a strategy. Minnal comes with a `CAS <https://github.com/Jasig/cas>`_ authenticator and allows you to build custom ones.

CAS Authenticator
-----------------
The CAS authenticator uses `Jasig client library <https://github.com/Jasig/cas>` to talk to the CAS server. It takes in a ``session/proxy ticket`` as credential and validates it against the request path. If they match, returns the CAS user that was originally authenticated. You can configure the authenticator in the security configuration of your application.

.. literalinclude:: application_config.yml
	:language: yaml
	:linenos:
	:lines: 20-28

To enable CAS authentication for your application, you will have to register CasPlugin in your application.

.. code-block:: java
	:linenos:

	@Override
	protected void registerPlugins() {
		if (getConfiguration().getSecurityConfiguration() != null) {
			registerPlugin(new CasPlugin(getConfiguration().getSecurityConfiguration()));
		}
		registerPlugin(new JPAPlugin());
	}

Ticket Store
~~~~~~~~~~~~
Minnal comes with a database store ``org.minnal.security.auth.cas.JpaPgtStorage`` for storing the proxy granting tickets. You can also provide a custom storage via the configuration by extending ``org.jasig.cas.client.proxy.ProxyGrantingTicketStorage``

Custom Authenticators
---------------------
Creating authenticators is simple. You will have to implement the interface ``org.minnal.security.auth.Authenticator`` and configure it in the security configuration of your application.

.. code-block:: java
	:linenos:

	public interface Authenticator<C extends Credential, P extends Principal> {

		P authenticate(C credential);
	}

Authorizers
===========
*Authorizers* authorize the incoming requests against the permissions defined over the route. Minnal comes with a simple authorizer ``org.minnal.security.auth.SimpleAuthorizer`` that reads the roles and permission of the user from property files. You can always extend it to provide your own authorizer.

The security configuration defaults to SimpleAuthorizer. In case you want to provide a custom authorizer implementation, you can override it in the application cofiguration.

.. code-block:: yaml
	:linenos:

	security:
	  sessionStore:
	    class: org.minnal.security.session.JpaSessionStore
	  authorizer:
	    class: your.custom.authorizer.CustomAuthorizer

Setting Permissions to Routes
-----------------------------
Minnal takes the permission configuration for the routes via the route definition. Below code shows a sample usage,

.. code-block:: java
	:linenos:

	public class OrderApplication extends Application<OrderConfiguration> {
	      @Override
	      protected void defineRoutes() {
	            resource(OrderResource.class).builder("/hello").action(HttpMethod.GET, "helloWorld")
	            .attribute(Authorizer.PERMISSIONS, "permission1,permission2");
	      }
	}

This instructs minnal to allow only the users with permissions *permission1* and *permission2* for the route *GET /hello*

Simple Authorizer
-----------------
The simple authorizer uses the roles and permissions of the principal ``org.minnal.security.auth.Principal`` to authorize the route. If the roles and permissions are not populated in the principal, it looks for the property files *user_roles.properties* and *role_permissions.properties* in the classpath and validates against them.

The location of the property files can be customized by overriding them in the configuration,

.. code-block:: yaml
	:linenos:

	security:
	  authorizer:
	    class: org.minnal.security.auth.SimpleAuthorizer
	    roleMapper: 
	      class: org.minnal.security.auth.SimpleUserRoleMapper
	      mappingFile: <location-to-property-file>
	    permissionMapper: 
	      class: org.minnal.security.auth.SimpleRolePermissionMapper
	      mappingFile: <location-to-property-file>

Custom Authorizers
------------------
You can customize the simple authorizer to fetch the roles and permissions of the user from a data store instead of from a property file. All you have to do is to implement the interfaces ``org.minnal.security.auth.UserRoleMapper`` and ``org.minnal.security.auth.RolePermissionMapper`` and override them in the authorizer configuration.

.. code-block:: yaml
	:linenos:

	security:
	  authorizer:
	    class: org.minnal.security.auth.SimpleAuthorizer
	    roleMapper: 
	      class: <your-custom-role-mapper-implementation>
	    permissionMapper: 
	      class: <your-custom-permission-mapper-implementation>

White listing urls
==================
You can whitelist the urls that you don't want to be autneticated by adding them to ``whiteListedUrls`` in configuration. Typical use cases are publicly visible files, health check urls etc.

.. literalinclude:: application_config.yml
	:language: yaml
	:linenos:
	:lines: 20, 29-30
