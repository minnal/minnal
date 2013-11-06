.. _getting-started:

###############
Getting Started
###############

This guide will serve as basic introduction to installing Minnal and take you through the process of creating a simple Minnal project: *Shopping cart*. We will also take you through some of the important concepts and good practices around *domain modeling* along the way.

Overview
========
Minnal is a highly scalable and productive RESTful service framework that helps you eliminate boiler plate code and build services faster. Minnal is a *Tamil* noun, meaning **"lightning"**. It allows you to build services at lightning speed through its powerful *instrumentation* module. It generates the API's for your resources at runtime and eliminates the need to write *controller* and *data access* layers for your service leaving you to focus on **enriching your domain**

Minnal comes with *out-of-the-box* support for **persistence**, **configuration**, **logging**, **metrics**, **API documentation** and is extremely powerful for services that are mostly *CRUD* centric but doesn't restrict you from using it for business interfaces as well.

A typical REST service consist of a *controller layer* that handles the incoming request, optionally delegates them to the *application layer* which in turn call the *domain layer* and *repository layer*. Most of these are boilerplate code and the real work happens in the domain layer and application layer. However good is the REST framework, it may not help you avoid writing the boilerplate code and you will have spend your effort in testing and managing these code. This is what Minnal is attempting to solve. It eliminates the DAL and Controller from your application, so you can focus on enriching your domain.

The framework has a modular design that makes every functionality easily pluggable. If you wish Minnal not to generate the API's for you, you can exclude the *instrumentation* module from you application. If you wish to write your own DAL, you could do well so by excluding the *JPA* module. The framework exposes hooks at different stages of the application lifecycle and can be easily extended to support custom functionalities.

Minnal uses `Netty <http://netty.io/>`_, a NIO client-server framework, underneath to achieve high scalability. `Jackson <http://jackson.codehaus.org/>`_ is used for serializing json/xml and `Snakeyaml <https://code.google.com/p/snakeyaml/>`_ for serializing yaml. Minnal recommends JPA for all DB access and uses `FlywayDB <http://flywaydb.org/>`_ for running db migrations.

Domain Modeling
---------------
We believe a very good domain model makes an application easily manageable. Minnal borrows a lots of concepts from `Domain Driven Design <http://dddcommunity.org/learning-ddd/what_is_ddd/>`_ and enforces it from within the framework.

Installing Minnal
=================
Prerequisites
-------------
To run minnal, you need JDK 6 or later. Minnal has been tested with Sun JDK, OpenJDK support will be taken up in the coming versions. Minnal recommends you to use maven to manage your project dependencies, the installation package requires `maven 3 <http://maven.apache.org/download.cgi>`_ to get started.

Ensure your **JAVA_HOME is set to JDK** and not the JRE. Minnal instrumentation module requires tools.jar from JDK libraries to be in the classpath.

Downloading the package
-----------------------
Download the latest minnal installation package from `here <https://github.com/minnal/minnal/releases/download/minnal-1.0.7/minnal-1.0.7.tar.gz>`_ and extract the archive to a folder with read and write access.

Add the the framework binary folder /path/to/minnal-X.X.X/bin to your path, so you can conveniently run the minnal executable. If you are running on windows, add it to the global environment variables

Test if you can run minnal,

.. code-block:: bash
   :linenos:

   $ minnal -help

If the installation is proper, you should be able to see the usage. Ensure you have executable permissions for the /path/to/minnal-X.X.X/bin folder

.. code-block:: bash
	:linenos:

	Usage: minnal [options] [command] [command options]

Tutorial
========
Let us get started by build a simple shopping cart application and in the process learn the core concepts of Minnal Framework.

Shopping Cart Application
-------------------------
Our shopping cart application will expose the below API's,

	* Create a new product
	* Get all the available products
	* Search the available products by title
	* Create a new shopping cart
	* Add items to shopping cart
	* Update items in the shopping cart
	* Delete items in shopping cart
	* Search for items in shopping cart

Creating the project
~~~~~~~~~~~~~~~~~~~~
Creating a new project is quite simple with minnal generators.

.. code-block:: bash
	:linenos:

	$ minnal new shopping-cart

This command will create a maven project, generate the application classes and a sample configuration file. You should be seeing the below logs when you run the command,

.. code-block:: bash
	:linenos:
	
	[INFO] Creating the project shopping-cart under /Users/ganeshs
	[INFO] Creating the folder src/main/java under /Users/ganeshs/shopping-cart
	[INFO] Creating the folder src/test/java under /Users/ganeshs/shopping-cart
	[INFO] Creating the folder src/main/resources under /Users/ganeshs/shopping-cart
	[INFO] Creating the folder src/test/resources under /Users/ganeshs/shopping-cart
	[INFO] Creating the folder src/main/resources/META-INF under /Users/ganeshs/shopping-cart
	[INFO] Creating the folder src/main/resources/META-INF/services under /Users/ganeshs/shopping-cart
	[INFO] Creating the container config file /Users/ganeshs/shopping-cart/src/main/resources/META-INF/container.yml
	[INFO] Creating the application spi file /Users/ganeshs/shopping-cart/src/main/resources/META-INF/services/org.minnal.core.Application
	[INFO] Creating the pom file pom.xml
	[INFO] Creating the file /Users/ganeshs/shopping-cart/src/main/resources/log4j.properties
	[INFO] Creating the application config file /Users/ganeshs/shopping-cart/src/main/resources/META-INF/shoppingcart.yml
	[INFO] Creating the file /Users/ganeshs/shopping-cart/src/main/java/com/shopping/cart/ShoppingCartConfiguration.java
	[INFO] Creating the file /Users/ganeshs/shopping-cart/src/main/java/com/shopping/cart/ShoppingCartApplication.java

We have just created a minnal project that can generate API's from JPA models. The application has been configured to connect to an in-memory HSQL database.

Creating the domain classes
~~~~~~~~~~~~~~~~~~~~~~~~~~~
Minnal provides generators for creating JPA models. Lets create the domain classes to the application to make it worth enough. The users of our application should be able to search the products, so lets start from there.

.. code-block:: bash
	:linenos:
	
	$ cd shopping-cart
	$ minnal generate Product -aggregateRoot -fields title:string:true price:double

This will create a Product class under the package com.shopping.cart.domain,

.. code-block:: bash
	:linenos:

	[INFO] Generating the model class Product under the package com.shopping.cart.domain
	[INFO] Creating the file /Users/ganeshs/shopping-cart/src/main/java/com/shopping/cart/domain/Product.java

We have marked the class Product as ``@AggregateRoot`` and hence it will be treated as a resource. We have also marked the field title as ``@Searchable``, so minnal will created API's with title in the search params.

We recommend you to read through `Domain Driven Design Quickly <http://www.infoq.com/minibooks/domain-driven-design-quickly>`_, a free ebook that explains in detail about identifying the *aggregate roots* in a domain. *"Aggregates are groups of entities that belong together and Aggregate Root is the entity that holds them all together. With out an aggregate root, other entities can't exist"*. With this definition, we can identify two Aggregate roots in our domain, Product and ShoppingCart. ShoppingCartItem belongs to ShoppingCart, without the cart, cart item can't exist. Product can be regarded as an aggregate root although it doesn't have any children under it as it can exist without a ShoppingCart and ShoppingCartItem.

The generated class will look like this,

.. code-block:: java
	:linenos:
	:emphasize-lines: 14,19

	package com.shopping.cart.domain;
 
	import org.minnal.jpa.entity.BaseDomain;
	import javax.persistence.Entity;
	import javax.persistence.Table;
	 
	import org.minnal.instrument.entity.*;
	 
	/**
	 * Product domain class
	 *
	 * @author Generated by minnal-generator
	 */
	@AggregateRoot
	@Entity
	@Table(name="products")
	public class Product extends BaseDomain {
	 
		@Searchable
		private String title;
	
		private Double price;
	 
		...
		...
	}

The base class BaseDomain extends the Model class from `ActiveJpa <https://github.com/ActiveJpa/activejpa>`_. *ActiveJpa is a java library that attempts to implement the active record pattern on top of JPA. It eliminates the need to create DAO or Repository classes and make programming DAL a lot more simpler*. Minnal recommends using JPA for DB access and provides a JPA plugin module with ActiveJpa integration.

Lets create the other domain classes for our application. We will need ShoppingCart and ShoppingCartItem classes to represent a shopping cart,

.. code-block:: bash
	:linenos:
	
	$ minnal generate ShoppingCart -aggregateRoot -fields customerId:string
	$ minnal generate ShoppingCartItem -fields quantity:integer

Again the class ShoppingCart is marked as @AggregateRoot and will contain ShoppingCartItems. Since ShoppingCartItem can't exist without a ShoppingCart, we are not marking it as @AggregateRoot. Lets start associating the entities together,

.. code-block:: java
	:linenos:	

	@AggregateRoot
	@Entity
	@Table(name="shopping_carts")
	public class ShoppingCart extends BaseDomain {
	 
		private String customerId;
	
		private Set<ShoppingCartItem> cartItems;
	 
		@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	  	@JoinColumn(name="shopping_cart_id")
	  	public Set<ShoppingCartItem> getCartItems() {
		    return this.cartItems;
		}
	 
	  	public void setCartItems(Set<ShoppingCartItem> cartItems) {
	    	this.cartItems = cartItems;
	  	}
	}

.. code-block:: java
	:linenos:

	@Entity
	@Table(name="shopping_cart_items")
	public class ShoppingCartItem extends BaseDomain {
	 
		private Product product;
	 
		private Integer quantity;
	 
	  	private ShoppingCart cart;
	 
	  	@ManyToOne(fetch=FetchType.LAZY)
	  	@JoinColumn(name="shopping_cart_id")
	  	public ShoppingCart getCart() {
	    	return this.cart;
	  	}
	 
	  	public void setCart(ShoppingCart cart) {
	    	this.cart = cart;
	  	}
	 
	  	@ManyToOne(fetch=FetchType.LAZY)
	  	@JoinColumn(name="product_id")
	  	public Product getProduct() {
	    	return this.product;
	  	}
	 
	  	public void setProduct(Product product) {
	    	this.product = product;
	  	}
	}

Test your APIs
~~~~~~~~~~~~~~
Minnal can generate test cases for the APIs it created. These tests may not be sufficient enough to certify your APIs but can ensure high level functionality is working.

.. code-block:: bash
	:linenos:

	minnal generate-tests -packages com.shopping.cart

This would create tests *src/test/java* for all your resources. You can run ``mvn test`` to test them

Starting the server
~~~~~~~~~~~~~~~~~~~

That's it. We are good to start the server now. **But wait, I haven't written my DAO and controller classes?** You don't have to write them, minnal generates the API's at runtime. Creating domain classes is the minimum task you have to do with minnal. Let us start the server and look at the generated API's

View your APIs
~~~~~~~~~~~~~~
The API's are generated during the startup on the application and will be available only during the application lifetime. Minnal generates the API's at runtime using bytecode enhancement techniques and doesn't generate the source code for you purposefully. Minnal has intentionally kept away from code generation as managing the generated code will be a mess. From our past experience, we have seem developers trying to modify the generated code and mess them up. Another reason why minnal doesn't generate code is to make the application look simpler with just domain code in it.

Minnal has support for `Swagger <https://github.com/wordnik/swagger-core/wiki>`_ api documentation. So you can view your api's from `swagger ui <http://swagger.wordnik.com/>`_. In the swagger ui, type in the url *http://localhost:8080/api/shoppingcarts/api-docs.json* to view the generated APIs

Customizing an API
~~~~~~~~~~~~~~~~~~
Say, we want to customize the create shopping cart api *POST /shopping_carts* to do additional stuff while creating the cart. We can override the api by creating a resource class for shopping cart. Minnal will generate all the API's but for the customized one. Let's try creating a ShoppingCartResource class under *org.minnal.examples.shoppingcart.resources*

.. code-block:: java
	:linenos:

	package com.shopping.cart.resources;
 
	import org.minnal.core.resource.Resource;
	import org.minnal.core.Request;
	import org.minnal.core.Response;
	import com.shopping.cart.domain.ShoppingCart;
	import org.jboss.netty.handler.code.http.HttpResponseStatus;
	 
	// The @Resource annotation tells minnal that this class is resource class.
	// The value parameter takes in an class that's marked with @AggregateRoot domain
	// Minnal will scan for all resources marked with @Resource
	@Resource(value=ShoppingCart.class)
	public class ShoppingCartResource {
	 
	  // We are overriding the api POST /shopping_carts.
	  // The method name should be the same as that of the one from
	  // the route definitions at http://localhost:8080/admin/routes/shoppingCart
	  public ShoppingCart createShoppingCart(Request request, Response response) {
	    // Your custom code here
	    response.setStatus(HttpResponseStatus.OK);
	    // The return value from this method will be set as response content
	  }
	}

When you customize an API, do make sure that you create a method with the name as defined in the routes definition from http://localhost:8080/admin/routes/shopping_carts. And don't forget to mark the class with @Resource annotation.

Now all calls to *POST /shopping_carts* will be hitting our custom resource class.

