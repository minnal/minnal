.. _manual-index:

==================
User Documentation
==================

.. highlight:: text

.. rubric:: This document will provide you detailed information about the feature set of the minnal modules, configuring them, organizing your project, 			testing your application, extending minnal etc...
			We suggest you read through :ref:`getting-started` to get an understanding of the framework capabilities if you haven't done yet.

High level design
=================

The core of minnal is the container that can run multiple applications. The notion of Container and Application might be confusing here. Unlike application servers like Tomcat, minnal doesn't create a classloader per application deployed. The figure below shows the high level design of the framework.

.. figure::  ../_static/images/minnal.png
   :align:   center

   High level design of Minnal Container

At the low level are the connectors that accept the incoming requests and routes them via the Router. Minnal comes bundled with HTTP and HTTPS connectors and allows you to extend to create a custom connector as well. The router resolves the route from the request and delegates to an application to handle it. The request will go through a set of filters before hitting the resource.

The container allows bundles to hook into its life cycle. Bundles can provide custom functionality at container level and will have access to the container. The application allows plugins to hook into its life cycle. Plugins can provide custom functionality at application level and will have access to all the resources and routes with in the application

Modules
=======
This section explains in detail the usage of various modules offered by minnal.

.. toctree::
    :maxdepth: 1

    core
    jpa
    migrations
    instrumentation
    security
    generator
    api
    metrics
    test
    examples
