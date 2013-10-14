.. _faq:

###
FAQ
###

Exceptions thrown
=================

ClassNotFoundException: com.sun.tools.attach.VirtualMachine
-----------------------------------------------------------
Minnal requires tools.jar to be present on the classpath. If it doesn't find, you might see the below exception,

.. code-block:: java
  :linenos:

  Caused by: java.lang.ClassNotFoundException: com.sun.tools.attach.VirtualMachine
  at java.net.URLClassLoader$1.run(URLClassLoader.java:202)
  at java.security.AccessController.doPrivileged(Native Method)
  at java.net.URLClassLoader.findClass(URLClassLoader.java:190)
  at java.lang.ClassLoader.loadClass(ClassLoader.java:306)
  at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:301)
  at java.lang.ClassLoader.loadClass(ClassLoader.java:247)
  ... 9 more

To resolve this, make sure your JAVA_HOME points to the JDK and not JRE. Also ensure that ``java`` command is loaded from JDK and not from JRE. If it still throws this exception, check if mvn is pointing to JDK.

