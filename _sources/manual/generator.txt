.. _manual-gen:

================
Minnal Generator
================
Minnal Generator provides a command line utility to generate the constructs of *Minnal*. The generator comes packaged as a binary. Click `here <http://minnal.github.io/minnal/getting-started.html#installing-minnal>`_ for instructions on installing the generator binary.

Create New Project
==============
.. code-block:: bash
	:linenos:

	$ minnal -help new

	Create a new minnal project
	Usage: new [options] The name of the project to create
	  Options:
	    -basedir
	       The dir under which the project has to be created
	       Default: /Volumes/data/flipkart/experiments/minnal/minnal-generator
	    -noadmin
	       Exclude admin application
	       Default: false
	    -noinst
	       Exclude instrumenation bundle
	       Default: false
	    -nojpa
	       Exclude Jpa plugin
	       Default: false
	    -version
	       The minnal version to use
	       Default: 1.0.4

Generate Models
===============
.. code-block:: bash
	:linenos:

	$ minnal -help generate-model

	Generates a model class
	Usage: generate-model(generate) [options] The name of the model to create
	  Options:
	    -aggregateRoot
	       Is this model an aggregate root?
	       Default: false
	    -fields
	       The fields in the model. Format name:type:searchable. Type is the java
	       type of the field (string, integer, long, short, char, double, float, date,
	       timestamp, boolean). Searchable is a boolean that specifies if the field is a
	       searchable field
	    -projectDir
	       The project directory
	       Default: /Volumes/data/flipkart/experiments/minnal/minnal-generator

Generate Tests
==============
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
	       Default: /Volumes/data/flipkart/experiments/minnal/minnal-generator

Start Server
============
.. code-block:: bash
	:linenos:

	$ minnal -help start

	Start the minnal project
	Usage: start [options] The minnal project directory
	  Options:
	    -projectDir
	       The project directory
	       Default: /Volumes/data/flipkart/experiments/minnal/minnal-generator

Other Options
=============
* ``Debug`` - Include -debug to enable DEBUG level
* ``Trace`` - Include -trace to enable TRACE level