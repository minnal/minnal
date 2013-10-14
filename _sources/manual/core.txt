.. _manual-core:

===========
Minnal Core
===========
.. highlight:: text

.. rubric:: *Minnal Core* is the core of the framework and doesn't depend on any of the other minnal modules. It is a light weight REST application 				framework and can be used independent of instrumentation module.

Organizing your project
=======================

Minnal recommends you to use maven for managing your project and its dependencies. If you are starting from scratch, take a look at :ref:`manual-gen` for creating a project. A minnal project structure would look like this,

Container
=========

Container Configuration
-----------------------

Minnal's configuration follows a composite pattern. `Routes`_ inherit from a Resource, `Resources`_ inherit from an Application and `Applications`_ inherit from Container. Minnal abstracts out a bunch of configuration parameters at container level and every component inherits them from their parent.

The configuration is loaded from a *container.yml* file located under *META-INF/* folder in the classpath

.. literalinclude:: container_config.yml
	:language: yaml
	:linenos:
	:lines: 1-11,18-

.. highlight:: text

.. hint::
	Take a look at the class ``org.minnal.core.config.ContainerConfiguration`` for all the available properties

Configuring SSL
---------------
Enabling https is quite similar to http, just that ssl needs to be configured. Create a https connector and configure ssl,

.. literalinclude:: container_config.yml
	:language: yaml
	:linenos:
	:lines: 7-17

.. hint::
	Take a look the class ``org.minnal.core.config.SSLConfiguration`` for all the available properties

Mounting applications to the container
--------------------------------------
To mount an application to the container, you will have add the application class to the SPI of org.minnal.core.Application. The container loads all the applications from the SPI file and mounts them as configured in the container.yml. Create a file org.minnal.core.Application under META-INF/services/ folder and the list of applications you want to add to the container,

.. code-block:: yaml
	:linenos:

	com.example.shopping.cart.ShoppingCartApplication

Applications
============
Application class is the entry point to your application. The application specific configurations are read from org.minna.core.config.ApplicationConfiguration class and you will have to extend it to create your application specific configuration

.. code-block:: java
	:linenos:

	package com.example;
 
	public class ExampleConfiguration extends ApplicationConfiguration {
	 
	}

You create an application by extending the class org.minnal.core.Application.

.. code-block:: java
	:linenos:

	package com.example;
 
	public class ExampleApplication extends Application<ExampleConfiguration> {
	 
	  @Override
	  protected void defineRoutes() {
	  }
	 
	  @Override
	  protected void defineResources() {
	    addResource(ExmapleResource.class);
	  }
	 
	  @Override
	  protected void addFilters() {
	  }
	 
	  @Override
	  protected void registerPlugins() {
	  }
	}

Application Configuration
-------------------------

Application inherits the configuration parameters from container. For instance, if you haven't set any serializers for json content type, it will get it from container. You can override it by explicitly specifying in the ApplicationConfiguration. Minnal looks for the application configuration under META-INF/ folder in the classpath. Be sure to name the file as lowercased application name. For instance if your application class name is ShoppingCartApplication, then the application config file name should be shoppingcart.yml. Bare minimum, the application config requires a name to be defined

.. literalinclude:: application_config.yml
	:language: yaml
	:linenos:
	:lines: 1-2

Database Configuration
----------------------
Minnal recommends you to use JPA in your persistence layer but the database configuration is generic enough to use any library in your persistence layer

.. literalinclude:: application_config.yml
	:language: yaml
	:linenos:
	:lines: 3-15

Minnal uses c3p0 for connection pooling. 

.. hint::
	Take a look at the class ``org.minnal.core.config.DatabaseConfiguration`` for all the available properties

Resources
=========
Creating a resource is straight forward. Unlike Application, resource class need not extend any framework specific classes. You create a resource under ``com.example.resources`` package.

.. code-block:: java
	:linenos:

	package com.example.resources;
 
	public class ExampleResource {
	 
	  /**
	   * Maps to the route GET /examples/:id
	   */
	  public void readExample(Request request, Response response) {
	    String id = request.getHeader("id");
	    Example example = null;
	    // Read example from your persistence store
	    response.setContent(example);
	    response.setStatus(HttpResponseStatus.OK);
	  }
	 
	  /**
	   * Maps to the route POST /examples/
	   */
	  public Example createExample(Request request, Response response) {
	    Example example = request.getContentAs(Example.class)
	    // Write example to your persistence store
	    return example;
	  }
	}

The only constraint the framework imposes on you is on the method signature of your resource methods. Your resource methods should take in an instance of ``org.minnal.core.Request`` and ``org.minnal.core.Resposne``. The method can return void or the content to set in the response. In case you return the content from the method, minnal will set the content and set the status to 200 OK. It will safely ignore the return value if the response content is already set.

Minnal's writes all the query and form parameters to request headers and you can access them by calling ``request.getHeader("param")``

The resource should be added to your Application to define the routes. You do this by adding the resource in the defineResources() method of the application class

.. code-block:: java
	:linenos:

	public class ExampleApplication extends Application<ExampleConfiguration> {
  
	  @Override
	  protected void defineResources() {
	    addResource(ExmapleResource.class);
	  }
	}

Resource Configuration
----------------------
By default minnal inherits the application configuration parameters in to the resource configuration. In most cases you won't have a need to customize these properties. But say if you want to accept the text/csv which is not the default content type specified by the application, you can customize the resource configuration by just pass on an instance of resource configuration to addResource method.

.. code-block:: java
	:linenos:

	@Override
	protected void defineResources() {
	  ResourceConfiguration configuration = new ResourceConfiguration("example resource");
	  configuration.addSerializer(MediaType.CSV_UTF_8, csvSerializer);
	  addResource(ExmapleResource.class, configuration);
	}

.. hint::
	Take a look at the class org.minnal.core.config.ResourceConfiguration for all the available properties

Routes
======
Routes are associated with resource classes. You add routes in the defineRoutes() method of the application class

.. code-block:: java
	:linenos:

	public class ExampleApplication extends Application<ExampleConfiguration> {
  
	  @Override
	  protected void defineRoutes() {
	    // Map ExampleResource.readExample method to the route GET /examples/{id}
	    resource(ExampleResource.class).builder("/examples/{id}").action(HttpMethod.GET, "readExample");
	 
	    // You can also chain the actions for a route
	    resource(ExampleResource.class).builder("/examples/{id}").action(HttpMethod.PUT, "updateExample").action(HttpMethod.DELETE, "deleteExample");                 
	  }
	}

Route Configuration
-------------------
By default minnal inherits the resource configuration parameters in to the route configuration. You can customize the route configuration as below,

.. code-block:: java
	:linenos:

	@Override
	protected void defineRoutes() {
	  RouteConfiguration configuration = new RouteConfiguration("some name");
	  configuration.addSerializer(MediaType.CSV_UTF_8, csvSerializer);
	 
	  // Map ExampleResource.readExample method to the route GET /examples/{id}
	  resource(ExampleResource.class).builder("/examples/{id}").action(HttpMethod.GET, "readExample").using(configuration);
	}

.. hint::
	Take a look at the class ``org.minnal.core.config.RouteConfiguration`` for all the available properties

Filters
=======
Minnal provides support for chaining of filters like in http-servlets. The request will pass through all the filters before hitting the controller. To create a filter, implement the interface ``org.minnal.core.Filter``

.. code-block:: java
	:linenos:

	public class MyFilter implements Filter {
 
	  @Override
	  public void doFilter(Request request, Response response, FilterChain chain) {
	    // pre process request
	    chain.doFilter(request, response);
	    // post process response
	  }
	}

Add Filters to Application
--------------------------
You can add filters to the application in the addFilters() method

.. code-block:: java
	:linenos:

	public class ExampleApplication extends Application<ExampleConfiguration> {
	  @Override
	  protected void addFilters() {
	    addFilter(new MyFilter());
	  }
	}

Exception Handlers
==================
Minnal defines a bunch of application exceptions that gets translated to meaningful HTTP response codes. These exceptions are defined under the package ``org.minnal.core.server.exception``. Below are some of them, take a look at the exception package for other exception types

* ``BadRequestException``: -- 400 Bad Request
* ``ConflictException``: -- 409 Conflict
* ``MethodNotAllowedException``: -- 405 Method Not Allowed

Mapping Exceptions
------------------
You can map your exceptions to an application exception. You have to override the method mapExceptions() in the application class to do this

.. code-block:: java
	:linenos:

	public class ExampleApplication extends Application<ExampleConfiguration> {
	  @Override
	  protected void mapExceptions() {
	    addExceptionMapping(PersistenceException.class, BadRequestException.class);
	    super.mapExceptions();
	  }
	}

Plugins
=======
Plugin hooks into the lifecycle of an application. Minnal invokes the plugin when an application is initialized or destroyed. Creating a plugin is fairly simple, you will just have to implement the interface ``org.minnal.core.Plugin``

.. code-block:: java
	:linenos:

	public class MyPlugin implements Plugin {
 
	  @Override
	  public void init(Application application) {
	    System.out.println("Initializing the plugin");
	  }
	 
	  @Override
	  public void destroy() {
	    System.out.println("Destroying the plugin");
	  }
	}

Register Plugin to Application
------------------------------
You can register a plugin in the registerPlugins() method of the application class

.. code-block:: java
	:linenos:

	public class ExampleApplication extends Application<ExampleConfiguration> {
	  
	  @Override
	  protected void registerPlugins() {
	    registerPlugin(new MyPlugin());
	  }
	}

.. hint::
	The registered plugins will be invoked in the same order they are registered

Bundles
=======
Bundles hook into the life cycle of the container. Bundle gets access to the container. The container invokes the bundle when the container is initialized, started, stopped. To create a bundle, simply implement the interface ``org.minnal.core.Bundle``

.. code-block:: java
	:linenos:

	public class MyBundle implements Bundle {
 
	  public void init(Container container) {
	    System.out.println("Intializing the bundle");
	  }
	 
	  public void start() {
	    System.out.println("Starting the bundle");
	  }
	 
	  public void stop() {
	    System.out.println("Stopping the bundle");
	  }
	}

Add Bundle to Container
-----------------------
To add a bundle to the container, you will have to add the bundle class to the SPI of ``org.minnal.core.Bundle`` under *META-INF/* folder,

.. code-block:: yaml
	:linenos:

	com.example.MyBundle

Application Context
===================
