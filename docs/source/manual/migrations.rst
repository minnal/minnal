.. _manual-migration:

=================
Minnal Migrations
=================

.. highlight:: text

.. rubric:: Minnal uses FlywayDB for database migrations. Read more about Flyway migrations `here <http://flywaydb.org/getstarted/firststeps/index.html>`_.			   The current version doesn't have full fledged integration with FlywayDB but provides a plugin to create the migrations during the startup of 			the application.

Create Migrations
=================
Refer `FlywayDB <http://flywaydb.org/getstarted/firststeps/index.html>`_ to read about creating migration files. With minnal, the migrations should be put into *src/main/resources/db/migration/* folder. Below are the migrations of our shopping cart application,

.. code-block:: sql
	:linenos:

	-- file: V1__create_products.sql
	create table products (
	  id integer not null primary key identity,
	  title varchar(50) not null,
	  price decimal(10, 2) not null
	);
	 
	-- file: V2__create_shopping_carts.sql
	create table shopping_carts (
	  id integer not null primary key identity,
	  customer_id varchar(50) not null
	);
	 
	-- file: V3__create_shopping_cart_items.sql
	create table shopping_cart_items (
	  id integer not null primary key identity,
	  product_id integer not null,
	  cart_id integer not null,
	  quantity integer not null
	);

Run Migrations
==============

You can either manually run the migrations using Flyway library (Refer its documentation on maven plugin) or register the plugin ``org.minnal.migrations.plugin.MigrationsPlugin`` in your application to run them during the start up of the application.

.. code-block:: java
	:linenos:

	public class ExampleApplication extends Application<ExampleConfiguration> {
  
	  @Override
	  protected void registerPlugins() {
	    registerPlugin(new MigrationsPlugin());
	    registerPlugin(new JPAPlugin());
	  }
	}
