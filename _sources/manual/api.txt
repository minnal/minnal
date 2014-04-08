.. _manual-api:

==========
Minnal Api
==========
Minnal has implemented `Swagger <https://github.com/wordnik/swagger-core/wiki>`_ specification for API documentation. You should be able to view your APIs from the `Swagger UI <https://github.com/wordnik/swagger-ui>`_.

Using Swagger UI
================
You can either download Swagger UI from `here <https://github.com/wordnik/swagger-ui>`_ or hit `http://swagger.wordnik.com/ <http://swagger.wordnik.com/>`_. In the text box, provide you application API url in the format ``http://localhost:8080/api/<application-name>/api-docs.json`` and hit enter. The application-name is the *name* you configured in your application config file under *META-INF/*

.. literalinclude:: application_config.yml
	:language: yaml
	:linenos: 
	:lines: 1-10
	:emphasize-lines: 1-2