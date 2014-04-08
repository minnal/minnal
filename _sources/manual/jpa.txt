.. _manual-jpa:

==========
Minnal Jpa
==========

.. highlight:: text

.. rubric:: *Minnal Jpa* has useful abstractions for your DAL. It makes use of ActiveJpa, a java library that attempts to implement the active record 				pattern on top of JPA. It eliminates the need to create DAO or Repository classes and make programming DAL a lot more simpler. Minnal 					recommends using JPA for DB access and provides a JPA plugin module with ActiveJpa integration.

Domain Classes
==============
Minnal Jpa provides a base domain class ``org.minnal.jpa.entity.BaseDomain`` which extends the ActiveJpa model class. Extending the base domain enables you to do ActiveRecord style operations like, ``Person.find(1L);``, ``Person.where("firstName", "Ganesh");`` etc...

.. code-block:: java
	:linenos:

	package com.example.domain;
 
	@Entity
	public class Example extends BaseDomain {
	 
	}

Configuring JPA
===============
Minnal Jpa provides a plugin ``org.minnal.jpa.JPAPlugin`` to configure JPA. You just need to register the plugin to your application

.. code-block:: java
	:linenos:

	public class ExampleApplication extends Application<ExampleConfiguration> {
  
	  @Override
	  protected void registerPlugins() {
	    registerPlugin(new JPAPlugin());
	  }
	}

The JPAPlugin reads the database configuration from your application configuration and loads the ``EntityManagerFactory``. You can configure the vendor specific properties in the db configuration section.

