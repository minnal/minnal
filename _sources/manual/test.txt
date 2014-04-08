.. _manual-test:

===========
Minnal Test
===========
Minnal test module provide abstractions for testing out projects built using minnal. While :ref:`manual-gen` generates the test cases for your resources, there are instances where you want to write additional tests to test customized APIs or test out your domain models. This section takes you through these abstractions using example functionalities.

Minnal enforces your domain models to extend from *ActiveJpa* models. AcitveJpa does runtime byte code isnstrumentation on your model classes and hence your models should be tested using `activejpa-test <https://github.com/ActiveJpa/activejpa#testing-your-models>`_. Minnal also uses `AutoPojo <https://github.com/minnal/Autopojo>`_ in the abstraction layer for auto populating the domain models. This eliminates you from constructing json/xml data test yourself.

.. note::
	
	Minnal test module supports only TestNG. Support for Junit is not there at this moment.

Testing Resources
=================
To test your resources, you should extend the abstract resource test class ``org.minnal.core.resource.BaseResourceTest``. The abstract class has helper methods to construct the request and parse the resposne for output comparisons.

.. code-block:: java
	:linenos:

	public class DummyResourceTest extends BaseResourceTest {

		@Test
		public void testPostRequest() {
			Response response = call(request("/dummy_resource/", HttpMethod.POST), "your content");
			assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
			DummyModel expected = new DummyModel();
			DummyModel actual = serializer.deserialize(response.getContent(), DummyModel.class);
			assertEquals(actual, expected);
		}

		@Test
		public void testGetRequest() {
			Response response = call(request("/dummy_resource/", HttpMethod.GET));
			assertEquals(response.getStatus(), HttpResponseStatus.OK);
			int actualCount = serializer.deserialize(response.getContent(), DummyModel.class).size();
			assertEquals(actualCount, 2);
		}
	}

Testing JPA Resources
=====================
Testing your JPA resources is bit tricky. You will have to ensure the a new transaction is opened before starting the test and rolled back at the end. Minnal provides an abstract test class for testing JPA resources ``org.minnal.core.resource.BaseJPAResourceTest``

.. code-block:: java
	:linenos:

	public class OrderResourceTest extends BaseJPAResourceTest {

		@Test
		public void listOrderTest() {
			org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
			order.persist();
			Response response = call(request("/orders/", HttpMethod.GET));
			assertEquals(response.getStatus(), HttpResponseStatus.OK);
			assertEquals(serializer.deserializeCollection(
					response.getContent(), java.util.List.class,
					org.minnal.example.domain.Order.class).size(),
					(int) org.minnal.example.domain.Order.count());
		}

		@Test
		public void createOrderTest() {
			org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
			Response response = call(request("/orders/", HttpMethod.POST,
					Serializer.DEFAULT_JSON_SERIALIZER
							.serialize(order)));
			assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
		}
	}

Testing Models
==============
To test your domain models, your test class should extend the class ``org.activejpa.entity.testng.BaseModelTest`` The base class takes care of byte code enhancement requirement of ActiveJpa and abstracts them out of your tests.

.. code-block:: java
	:linenos:

	public class OrderTest extends BaseModelTest {
		@Test
		public void testCreateOrder() {
			Order order = new Order();
			order.setCustomerEmail("dummyemail@dummy.com");
			order.persist();
			Assert.assertEquals(Order.where("customer_email", "dummyemail@dummy.com").get(0), order);
		}
	}

Take a look at `activejap-test <https://github.com/ActiveJpa/activejpa#testing-your-models>`_ for more information on testing your models and for additional exmaples.