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

White listing urls
==================
You can whitelist the urls that you don't want to be autneticated by adding them to ``whiteListedUrls`` in configuration. Typical use cases are publicly visible files, health check urls etc.

.. literalinclude:: application_config.yml
	:language: yaml
	:linenos: 
	:lines: 20, 29-30