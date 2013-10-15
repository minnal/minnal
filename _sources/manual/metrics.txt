.. _manual-metric:

==============
Minnal Metrics
==============
Metrics bundle hooks into the container and collects statistics at application level. Minnal metrics internally uses `Yammer's metric <http://metrics.codahale.com/>`_ library and allows you to log the metrics using multiple reporters.

To enable metrics in your service, add minnal-metrics to your maven dependencies.

.. code-block:: xml
	:linenos:

	<dependency>
			<groupId>org.minnal</groupId>
			<artifactId>minnal-metrics</artifactId>
	</dependency>

And enable one or more reporters in the container configuration. You will have to overirde the bundle configuration for metrics bundle. Below code shows a sample configuration 

.. literalinclude:: container_config.yml
	:language: yaml
	:linenos:
	:lines: 32-38

Response Time Collector
=======================
The Response time collector collects the response times at route level for each of the applications.  It also computes the max, min, mean, rate and percentile information for every route. The collected metrics are logged via the configured Reporters for every incoming request.

Metric Key
----------
The metric key is a combination of application name and the route pattern. For instance, if the route pattern is ``GET /shopping_carts/{id}/shopping_cart_items`` and the application name is *shoppingcarts*, then the metric key will be 
	
	shoppingcarts.shopping_carts.id.shopping_cart_items.GET.responseTime

Datasource Stats Collector
==========================
The Datasource collector collects the connection pool statistics like **active/idle/total connections**, **failed checkins/checkouts/idle tests**, **uptime** etc. and logs them when requested by the reporters.

Metric Key
----------
The datasource metrics are logged using the key,
	
	<application-name>.datasource

JMX Reporter
============
JMX Reporter can be enabled from container configuration. 

.. literalinclude:: container_config.yml
	:language: yaml
	:linenos:
	:lines: 32-38

You can view metrics under the JMX domain ``metrics`` using Jconsole. 

Graphite Reporter
=================
Graphite Reporter can also be enabled from container configuration. It allows you to customize the graphite host/port, key prefix and poll period.

.. literalinclude:: container_config.yml
	:language: yaml
	:linenos:
	:lines: 32-

Custom Reporters
================
The current design doesn't allow you to create custom reporters in an elegant way. Please raise an issue about your requirement, we will take it up.

Take a look at the class ``org.minnal.metrics.DataSourcePoolMetricCollector`` for inspiration.

Publishing App Metrics
======================
You can also publish you application specific metrics using *Minnal Metrics*. Minnal creates a MetricRegistry per application to which you can publish your metrics. Below is a demonstration of this functionality.

.. code-block:: java
	:linenos:

	MetricRegistry metricRegistry = MetricRegistries.getRegistry(application.getConfiguration().getName());
	metricRegistry.register(MetricRegistry.name(application.getConfiguration().getName(), "orders", "shipped"),
        new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return Orders.count("status", "shipped");
            }
        }
	);
