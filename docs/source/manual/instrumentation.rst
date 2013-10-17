.. _manual-inst:

======================
Minnal Instrumentation
======================
The instrumentation module is where all the magic happens. Minnal enforces some of the key concepts from Domain Driven Design from within in the framework. The notion of aggregates and aggregate roots is the base of the instrumentation module. Minnal creates the resources and routes around this principle.

Resources
=========
Converting your domain models to resources is as simple as marking them with an annotation. The ``@AggregateRoot`` annotation instructs minnal to convert the domain model to a resource, construct routes and expose them as API's. The only constriant minnal imposes is your domain models should extend ``org.minnal.jpa.domain.BaseDomain`` class. This class extends the ``ActiveJpa`` Model which is based on ActiveRecord pattern and makes the instrumentation look simpler. 

.. code-block:: java
	:linenos:

	@Entity
	@AggregateRoot
	public class Order extends BaseDomain {

	}

The resource name is automatically derived from the domain model name. Minnal pluralizes the domain class name and converts them to underscore separated string.

.. note::

	Minnal follows underscore convention for the genrated apis and recommends the same convention for the APIs created manually as well

CRUD
----
The ``@Aggregate`` annotation creates all the CRUD APIs dynamically at the *runtime*. Minnal creates resource classes at the startup time of the application and maps the routes to the resource methods. 

Create
~~~~~~
All the create APIs accept the ``HTTP POST`` method and a payload to create the object. It is must to specify the content-type of the payload in the request. The route for create calls will be of the format,

.. code-block:: bash
	:linenos:

	POST /<resource-name>
	Content-Type: <content-type-of-payload>

	<your-payload>

Read
~~~~
The read calls can be classified into fetch and search. A fetch operation fetches resources by identifiers and a search operation results in returning multiple entities matching the search criteria. The content-type header is not mandatory for read calls, it defaults to the ``defaultMediaType`` property in the configuration. 

.. literalinclude:: container_config.yml
	:language: yaml
	:linenos:
	:lines: 19-20

The route for read calls will be of the format,

.. code-block:: bash
	:linenos:

	# Search call
	GET /<resource-name>?<search-param>=<value>&.. 
	
	# Fetch call
	GET /<resource-name>/{id}

Update
~~~~~~
Updates are always done at entity level. The content-type header is mandatory for update calls. The route for update calls will be of the format,

.. code-block:: bash
	:linenos:

	PUT /<resource-name>/{id}
	Content-Type: <content-type-of-payload>

	<your-payload>

Delete
~~~~~~
Updates are always done at entity level. The route for update calls will be of the format,

.. code-block:: bash
	:linenos:

	DELETE /<resource-name>/{id}

Sub-Resources
=============
Minnal identifies the sub-resources by the JPA annotations ``@OneToMany`` and ``@ManyToMany``. Any field or getter marked with these annotations will turn into a sub-resource.

.. code-block:: java
	:linenos:

	@Entity
	@AggregateRoot
	public class Order extends BaseDomain {

		@OneToMany
		@JoinColumn(name="orderId")
		private Set<OrderItem> orderItems;
	}

	@Entity
	public class OrderItem extends BaseDomain {

		@OneToMany
		@JoinColumn(name="orderItemId")
		private Set<Attribute> attributes;
	}

In the above example, minnal would create a root resource for orders and two sub-resources one at item level and other at item attribute level.

.. code-block:: bash
	:linenos:

	/orders
	/orders/{order_id}/order_items 
	/orders/{order_id}/order_items/{order_item_id}/attributes

.. hint::
	
	While dealing with bi-directional associations, you will be seeing stack overflow errors during serialization. This is not a minnal specific issue but an issue with the modeling in general. One of the common practices being followed is to switch off the receiving end in the bi-directional association. Jackson provides a couple of solutions via ``@JsonIgnore`` and ``@@JsonManagedReference & @JsonBackReference`` annotations.

Exclude Routes
==============
You can optionally exclude some of the routes from being exposed as an API. For instance, you may not want to support a DELETE operation on your resource or an UPDATE operation on your sub-resources. The ``@AggregateRoot`` annotation takes in optional parameters to exclude crud operations for root resource.

.. code-block:: java
	:linenos:

	// This aggregate root will expose only read apis
	@Entity
	@AggregateRoot(create=true, update=false, delete=false, read=true)
	public class Order extends BaseDomain {

	}

In the above case, the *Order* resource will only expose create & read operations and update/delete will be blocked.

Exclusion can also be done at the collection (sub-resource) level. You will have to annotate your collections using ``@Collection`` annotation and override the crud values for the colleciton.

.. code-block:: java
	:linenos:

	// This aggregate root will expose only read apis
	@Entity
	@AggregateRoot(create=false, update=false, delete=false, read=true)
	public class Order extends BaseDomain {

	   // The order items collection read api wont be exposed
	   @Collection(read=false, delete=false, update=false)
	   private Set<OrderItem> orderItems;
	}

In this case, the *OrderItem* sub-resource will expose only create API. Others will be blocked.

Overriding Routes
=================
There could be scenarios where you want to provide a custom `create` functionality but use the default ones for the reads and updates. Minnal allows you to override the generated routes to address these cases. Overriding a route is as simple as you implementing a resource method using any other framework. You will have to create a Resource class and implement the overridden route.

Say, we want to customize the create shopping cart api POST /shopping_carts to do additional stuff while creating the cart. We can override the api by creating a resource class for shopping cart. Minnal will generate all the API’s but for the customized one. Let’s try creating a ShoppingCartResource class under org.minnal.examples.shoppingcart.resources

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

When you customize an API, do make sure that you create a method with the name as defined in the routes definition from http://localhost:8080/admin/routes/shopping_carts. And don’t forget to mark the class with @Resource annotation.

Now all calls to POST /shopping_carts will be hitting our custom resource class.

Search Params
=============
Search is one of the key functionalities in a web service. Minnal offers sophisticated solution for searching through entities through its ``@Searchable`` annotation. You can mark the fields in your entity that you want to be searchable with *@Searchable* annotation and minnal would start expecting them in the query params. Minnal makes use of `ActiveJpa filters <https://github.com/ActiveJpa/activejpa>`_ internally to search the entities.

.. code-block:: java
	:linenos:

	@Entity
	@AggregateRoot
	@Access(AccessType.FIELD)
	public class Order extends BaseDomain {

		@OneToMany
		@JoinColumn(name="orderId")
		private Set<OrderItem> orderItems;

		@Searchable
		private String customerEmail;
	}

In the Order resource above, the field *customerEmail* is marked as searchable and would eventually show up in the GET APIs of order resource. You should be able to filter orders by customer's email by passing on the email in the query params.

.. code-block:: bash
	:linenos:

	# Returns all the orders for the customer with email id 'ganeshs@flipkart.com'
	GET /orders?customer_email=ganeshs@flipkart.com

	# Checks if order id 1000 was ordered by ganeshs@flipkart.com
	GET /orders/1000?customer_email=ganeshs@flipkart.com

Nested Search Params
--------------------
You can also filter parent records based on child's data. Minnal allows nesting of query parameters, means you should be able to get all orders that has items with certain categiry ``?orders.order_items.category=books``. This is achieved by marking the fields in child entities with *@Searchable*,

.. code-block:: java
	:linenos:

	@Entity
	@AggregateRoot
	@Access(AccessType.FIELD)
	public class Order extends BaseDomain {

		@OneToMany
		@JoinColumn(name="orderId")
		private Set<OrderItem> orderItems;

		@Searchable
		private String customerEmail;
	}

	@Entity
	@Access(AccessType.FIELD)
	public class OrderItem extends BaseDomain {

		@Searchable
		private String category;
	}

.. note::
	
	@Searchable can be added only on fields that translate to a database column. Adding it on a collection or association or a transient column might lead to SQL errors.

.. note::
	
	The nested searhc params should include the prefix of the root resource. For instance if the root is */orders*, the nested search param should be  prefixed with *orders.*

.. note::
	
	Be aware that Minnal uses underscore convention for resources, search params, payload etc. Using wrong case convention might lead to incorrect results. 

Actions
=======
Minnal can convert your domain operations to APIs effortlessly. You can mark your domain operations with the annotation ``@Action`` to indicate that it has to be exposed as PUT api and minnal will create a route for that method and list them in the APIs.

Domain Driven Design insists that all the operations in an aggregate go through the root of the aggregate. This ensures data integrity and enforcing invariants. For instance, say you want to cancel an order item in an order, it will be easier to enforce the invariants like *allow item cancellation only if order is not yet approved*. Or in other words, if your controller invokes ``orderItem.cancel()``, it may not be easy to impose order level invariants to it. But if you call ``order.cancelOrderItem(orderItem)``, you get the flexibility of verifying all the order level invariants before cancelling orderItem.

Aggregate Root Action
---------------------
Below code shows a simple usage of @Action on an order cancel operation.

.. code-block:: java
	:linenos:

	@Action(value="cancel")
	public void cancel(String reason) {
	    setStatus(Status.cancelled);
	    setCancellationReason(reason);
	}

This would create a route ``PUT /orders/{order_id}/cancel`` that would accept the follwing payload,

.. code-block:: javascript
	:linenos:

	{"reason": "some cancellation reason"}

Minnal automatically populates the keys in the payload to the method arguments of the domain operation. 

.. hint::
	
	You can control the api name by changing the @Action annotation attribute *value*.

Aggregate Child Action
----------------------
In case of actions on an aggregate child entity, you might want the path to be at the child resource level. A cancel operation on order item should have a route ``PUT /orders/{order_id}/order_items/{order_item_id}/cancel``. You can control the route path by specifying the @Action annotation attribute *path*. The path should be a relative path from root to the child separated by DOTS. ``Ex: orderItems.shipments``

.. code-block:: java
	:linenos:

	@Action(value="cancel", path="orderItems")
	public void cancelOrderItem(OrderItem orderItem, String reason) {
	    orderItem.cancel(reason);
	}

This would create a route ``PUT /orders/{order_id}/order_items/{order_item_id}/cancel`` that would accept the follwing payload,

.. code-block:: javascript
	:linenos:

	{"reason": "some cancellation reason"}

Minnal automatically populates the orderItem in the method argument from the request path and method arguments from the payload. 

.. warning::

	Bulk operations are not supported currently.

Pagination
==========
Minnal has an *out-of-the-box* support for pagination. You can pass on the page number and per page count with any GET request to filter of the result. Below code shows a sample pagination request and resposne.

.. code-block:: bash
	:linenos:

	GET /orders?customer_email=ganeshs@flipkart.com&page=1&per_page=10

	{
	   "page": 1,
	   "per_page": 10,
	   "total": 125,
	   "count": 10,
	   "data":  []
	}

.. note::
	
	If the per_page and page query parameters are not set, the response structure will be different. Only the *data* part will be present in the resposne. Do ensure that you treat them differently in your clients.


Bulk Operations
===============
Minnal supports bulk CRUD operations *out-of-the-box*. They should suffice for most of the use cases but if you feel a need to customize them, you will have to override the functionality.

Bulk Create
-----------
You can create multiple objects in a single request by passing on an array in the POST body. Minnal will create all the objects in a single transactional scope.

.. code-block:: javascript
	:linenos:

	 POST /orders/1/order_items

	[{
	    "order_id": 1,
	    "quantity": 2,
	    "product_id": "xyz"
	}, {
	    "order_id": 1,
	    "quantity": 1,
	    "product_id": "abc"
	}]

.. note::

	The response body for the create request will be different for bulk vs single creates. In case of bulk, you will get an array back

Bulk Read
---------
You can ready multiple entities in a single request by passing on comma-separated idntifiers in the request path. Minnal will return back an array of objects matching the identifiers in the response.

.. code-block:: javascript
	:linenos:

	GET /orders/1,2

	[{
	    "id": 1,
	    "customer_email": "ganeshs@flipkart.com"
	 }, {
	    "id": 2,
	    "customer_email": "ganeshs@flipkart.com"
	}]

	GET /orders/1/order_items/12,13

	[{
	    "id": 12,
	    "order_id": 1,
	    "quantity": 1
	 }, {
	    "id": 13,
	    "order_id": 1,
	    "quantity": 1
	}]

.. note::
	
	The response body for the get request will be different for bulk vs single reads. In case of bulk, you will get an array back

Bulk Update
-----------
Like in bulk read, you can pass on a comma-separated identifiers to update them in bulk. The payload in the PUT call will be applied for all the entities. This might be a limitation in some cases, you will have to customize the api in such scenarios. All the entities will be updated in the same transactional scope.

.. code-block:: javascript
	:linenos:

	PUT /orders/1,2,3

	{
	  "customer_email": "ganeshs@flipkart.com"
	}

Bulk Delete
-----------
Bulk delete is similar to bulk retrieval, just pass on comma-separated identifiers that you want to delete.

.. code-block:: javascript
	:linenos:

	DELETE /orders/1/order_items/12,13
