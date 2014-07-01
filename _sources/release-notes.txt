.. _release-notes::

#############
Release Notes
#############

minnal-1.2.0 / 1-Jul-2014
==========================
This release has upgrades to the latest version of Swagger and ActiveJPA. With these upgrades, minnal has support for Swagger spec version 1.2 and JPA 2.1.

Note: The swagger base url has changed from this version. It will be of the format /api/<application-name>/api-docs 

* Fixed issue `#93 <https://github.com/minnal/minnal/issues/93>`_ (Collection name singularization not proper)
* Fixed issue `#94 <https://github.com/minnal/minnal/issues/94>`_ (Upgrade activejpa to latest to support JPA2.1)
* Fixed issue `#84 <https://github.com/minnal/minnal/issues/84>`_ (Upgrade to latest version of swagger-core)

minnal-1.1.3 / 8-Apr-2014
==========================

* Fixed issue `#87 <https://github.com/minnal/minnal/issues/87>`_ (FD leak in HttpsConnector)

minnal-1.1.2 / 24-Dec-2013
==========================

* Fixed issue `#80 <https://github.com/minnal/minnal/issues/80>`_ (Upgrade to latest version of Active Jpa)

minnal-1.1.1 / 22-Nov-2013
==========================

* Fixed issue `#79 <https://github.com/minnal/minnal/issues/79>`_ (Upgrade autopojo to the latest version 0.0.7)

minnal-1.1.0 / 11-Nov-2013
==========================
A bunch of issues have been fixed in this release. Also added a sample petclinic application (from spring examples) using minnal.

* Fixed issue `#69 <https://github.com/minnal/minnal/issues/69>`_ (Compilation failures in Jdk7)
* Fixed issue `#70 <https://github.com/minnal/minnal/issues/70>`_ (Simple Authorizer throws NPE when property files are not there)
* Fixed issue `#72 <https://github.com/minnal/minnal/issues/72>`_ (Minnal test generator replaces the test classes)
* Fixed issue `#73 <https://github.com/minnal/minnal/issues/73>`_ (Add minnal-api dependency to the project generator)
* Fixed issue `#74 <https://github.com/minnal/minnal/issues/74>`_ (Create a petclinic sample application)
* Fixed issue `#77 <https://github.com/minnal/minnal/issues/77>`_ (Support for adding multiple jackson modules to custom serializers)
* Fixed issue `#78 <https://github.com/minnal/minnal/issues/78>`_ (Move minnal-example to examples project)

minnal-1.0.7 / 07-Nov-2013
==========================
This release has fixes for the below blocker bugs. 

* Fixed issue `#67 <https://github.com/minnal/minnal/issues/67>`_ (tools.jar dependency issue on multiple platforms)
* Fixed issue `#68 <https://github.com/minnal/minnal/issues/68>`_ (Maven dependency resolution failure on org.eclipse.equinox)


minnal-1.0.6 / 05-Nov-2013
==========================

* Fixed issue `#30 <https://github.com/minnal/minnal/issues/30>`_ (Build support for Authorization)

  **Authorizing the routes**

  This version has got support for authorizing the routes with annotations (if you use minnal-instrumentation) and without annotations (via the route definitions). To use the authorization support, you will have to include minnal-security in your pom.xml file.

  Below is a sample code to set up auth permissions for routes via the route definition,

  .. code-block:: java
	:linenos:

	public class OrderApplication extends Application<OrderConfiguration> {
	      @Override
	      protected void defineRoutes() {
	            resource(OrderResource.class).builder("/hello").action(HttpMethod.GET, "helloWorld")
	            .attribute(Authorizer.PERMISSIONS, "permission1,permission2");
	      }
	}      

  The same can be achieved through annotations using the minnal-instrumentation module,

  .. code-block:: java
	:linenos:

	@AggregateRoot
	@Entity
	@Table(name="Orders")
	@SecureMultiple({
	    @Secure(method=Method.POST, permissions="CREATE_ORDER"),
	    @Secure(method=Method.PUT, permissions="EDIT_ORDER")
	})
	public class Order extends Model {

	    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	    @JoinColumn(name="orderId")
	    @JsonManagedReference
	    @Secure(method=Method.DELETE, permissions="DELETE_ITEM")
	    private Set<OrderItem> orderItems = new HashSet<OrderItem>();

	    @Action(value="cancel")
	    @Secure(method=Method.PUT, permissions="CANCEL_ORDER")
	    public void cancel(String reason) {
	        setStatus(Status.cancelled);
	        setCancellationReason(reason);
	    }
	}  

* Fixed issue `#61 <https://github.com/minnal/minnal/issues/61>`_ (Fixes in case conversion from under_scores to camelCase)

minnal-1.0.5 / 22-Oct-2013
==========================

* Fixed issue `#49 <https://github.com/minnal/minnal/issues/49>`_ (Support including & excluding fields in the json/xml response)

  **Dynamically including and excluding fields in the response**

  This version of minnal has support for dynamically including or excluding fields in the json response. This is quite useful when returning back a deeply nested entity or entities with collections. If combined with JPA lazy loading, you can avoid firing unwanted db sqls and increase the performance of the API. Below is a sample usage

  .. code-block:: bash
	:linenos:

	GET /orders?exclude=created_at,order_items,payments
	GET /orders?include=order_items

* Fixed issue `#57 <https://github.com/minnal/minnal/issues/57>`_ (Unable to create manual routes to an auto created resource)
* Fixed issue `#59 <https://github.com/minnal/minnal/issues/59>`_ (Swagger API doesn't show operations for applications with base path other than '/')
* Fixed issue `#60 <https://github.com/minnal/minnal/issues/60>`_ (404 errors are not thrown)


minnal-1.0.4 / 11-Oct-2013
==========================
* Fixed issue `#55 <https://github.com/minnal/minnal/issues/55>`_ - Random test failures

minnal-1.0.3 / 10-Oct-2013
==========================

* Fixed issue `#54  <https://github.com/minnal/minnal/issues/54>`_ - Move maven repo

  Minnal uses github for hosting the maven artifacts. The artifacts were pushed to a different branch in the minnal project and this has become a bottleneck now as the size of the repository has grown. This changed moves the maven repository to a different github project. Please change your maven repository in the pom file to the location below.

  .. code-block:: xml
	:linenos:

	<repository>
	  <id>minnal-releases-repo</id>
	  <url>https://raw.github.com/minnal/mvn-repo/master/releases</url>
	</repository>

	<repository>
	  <id>minnal-snapshots-repo</id>
	  <url>https://raw.github.com/minnal/mvn-repo/master/snapshots</url>
	</repository>

minnal-1.0.2 / 09-Oct-2013
==========================

* Fixed issue `#24  <https://github.com/minnal/minnal/issues/24>`_ - Implement @Action handler. PUT calls will invoke methods marked with this annotation

  **Auto generate routes for your domain operations**

  You can now generate routes for your domain operations using the annotation ``@Action``. A method marked with this annotation will automatically show up in the routes. This annotation is applicable only for domain models annotated with ``@AggregateRoot``. Minnal enforces the users to follow stringent domain modeling. Any operations involving the children of the aggregate root should be driven by the root. For instance if you want to cancel 5 quantities of an order item, you should call cancel(orderItem, 5) on order which in turn would call orderItem to cancel 5 quantities. This way, any domain check (like can the order item be cancelled in the current state of order etc.. ) can be done at order level.

  .. code-block:: java
  	:linenos:

  	/**
	 * This method will expose the route /orders/{order_id}/cancel
	 * Your payload should be a json structure with keys mapping to the name of the method arguments
	 * In this scenario the payload would be {"reason": "some cancellation reason"}
	 * Minnal will automatically call this method with the reason taken from payload
	 */
	@Action(value="cancel")
	public void cancel(String reason) {
	    setStatus(Status.cancelled);
	    setCancellationReason(reason);
	}

	/**
	 * This method will expose the route /orders/{order_id}/order_items/{order_item_id}/cancel
	 * Your payload should be a json structure with keys mapping to the name of the method arguments
	 * In this scenario the payload would be {"reason": "some cancellation reason"}
	 * Minnal will automatically call this method with the reason taken from payload
	 */
	@Action(value="cancel", path="orderItems")
	public void cancelOrderItem(OrderItem orderItem, String reason) {
	    orderItem.cancel(reason);
	}

* Fixed issue `#52  <https://github.com/minnal/minnal/issues/52>`_ - ApplicationConfig should be globally accessible within an application

  **Globally accessible application context**

  Minnal now allows configurations to be accessible from any where in the request flow. ``ApplicationContext`` will give you access to the configurations specific to the current request like ``RouteConfiguration``, ``ResourceConfiguration`` and ``ApplicationConfiguration``. Below is the sample usage,

  .. code-block:: java
  	:linenos:

  	ApplicationContext.instance().getApplicationConfiguration();
	ApplicationContext.instance().getResourceConfiguration();
	ApplicationContext.instance().getRouteConfiguration();

minnal-1.0.1 / 02-Sep-2013
==========================

* Fixed issue `#50 <https://github.com/minnal/minnal/issues/50>`_ - Support for excluding certain routes from the API

  **Support for excluding certain routes from the API**

  You can now exclude that routes that you don't want to expose to the clients from the API list. This can be done at the aggregate root level as well as at the collection level,

  .. code-block:: java
  	:linenos:

  	// This aggregate root will expose only read apis
	@Entity
	@AggregateRoot(create=false, update=false, delete=false, read=true)
	public class Order extends Model {

	   // The order items collection read api wont be exposed
	   @Collection(read=false)
	   private Set<OrderItem> orderItems;
	}

minnal-1.0.0 / 29-Aug-2013
==========================

* Fixed issue `#47 <https://github.com/minnal/minnal/issues/47>`_ - Encoded UI parameters are not decoded
* Fixed issue `#48 <https://github.com/minnal/minnal/issues/48>`_ - Minnal API goes on an infinite loop even when the birectional mapping has JsonBackReference

minnal-0.9.9 / 28-Aug-2013
==========================

* Fixed issue `#46 <https://github.com/minnal/minnal/issues/46>`_ - Minnal Generator misses out autopojo repository while creating new projects

minnal-0.9.8 / 27-Aug-2013
==========================

* Fixed issue `#15 <https://github.com/minnal/minnal/issues/15>`_ - Auto Generate Test cases

  **Auto generating test code**

  Minnal now can generate test cases for the routes it generated. This is a step towards the goal of speeding up the service development. The minnal-example module has test cases that are auto generated by minnal

  .. code-block:: bash
  	:linenos:

  	$ minnal -help generate-tests
	Generates the resource tests
	Usage: generate-tests [options]
	  Options:
	    -packages
	       The list of packages
	       Default: []
	    -projectDir
	       The project directory
	       Default: /Users/ganeshs/doc

  Sample usage,

  .. code-block:: bash
  	:linenos:

  	$ minnal generate-tests -packages com.example.shoppingcart

minnal-0.9.7 / 17-Aug-2013
==========================

* Fixed issue `#45 <https://github.com/minnal/minnal/issues/45>`_ - Swagger API documentation bug
* Fixed issue `#5 <https://github.com/minnal/minnal/issues/5>`_ - Support for bulk retrieval/create/update/delete

  **Support for bulk operations**

  This release will have support for bulk retrievals, updates, creates and deletes. Backward compatibility has been ensured and so you don't have to change your api's.

  *Bulk retrieval*

  When the identifiers are comma-seperated in the GET call, minnal would return back an array instead of a single object. When the identifiers are not separated by comma, a single object would be returned. A couple of examples below,

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

  *Bulk create*

  In the POST call, if an array is passed, minnal will iterate over the array and create each of them in a single transactional scope.

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

  *Bulk update*

  If you pass in a comma-separated identifiers, the same payload will be applied for all the objects resolved by the identifiers. Note: this assumes the payload is same for all the objects to be updated.

  .. code-block:: javascript
  	:linenos:

  	 PUT /orders/1,2,3

	 {
	   "customer_email": "ganeshs@flipkart.com"
	 }

  *Bulk delete*

  If identifiers are separated by comma, all of them will be deleted in the same transactional scope.

  .. code-block:: javascript
  	:linenos:

  	 DELETE /orders/1/order_items/12,13

minnal-0.9.6 / 12-Aug-2013
==========================

* Fixed issue `#44 <https://github.com/minnal/minnal/issues/44>`_ - Nested objects are not updated in the PUT call
* Fixed enhancement `#4 <https://github.com/minnal/minnal/issues/4>`_ - Support pagination in the list/search command

  **Pagination**

  This release has support for pagination in the search APIs. This change is completely backward compatible and shouldn't impact your existing APIs.

  .. code-block:: javascript
  	:linenos:

  	GET /orders?customer_email=ganeshs@flipkart.com&page=1&per_page=10

	{
	   "page": 1,
	   "per_page": 10,
	   "total": 125,
	   "count": 10,
	   "data":  []
	}
