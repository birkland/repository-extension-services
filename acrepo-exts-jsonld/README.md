Repository JSON-LD compaction service
=====================================

This collection of camel routes exposes an HTTP endpoint for
generating compact JSON-LD serializations of Fedora resources.

Building
--------

To build this project use

    gradle install

Deploying in OSGi
-----------------

This projects can be deployed in an OSGi container. For example using
[Apache Karaf](http://karaf.apache.org) version 4.x or better, you can run the following
command from its shell:

    feature:repo-add mvn:edu.amherst.acdc/acrepo-karaf/LATEST/xml/features
    feature:install acrepo-exts-jsonld

Configuration
-------------

The application can be configured by creating the following configuration
file `$KARAF_HOME/etc/edu.amherst.acdc.exts.jsonld.cfg`. The following values
are available for configuration:

In the event of failure, the maximum number of times a redelivery will be attempted.

    error.maxRedeliveries=10

The base url of the fedora repository

    fcrepo.baseUrl=localhost:8080/fcrepo/rest

The location of the JSON-LD context document

    jsonld.context=https://acdc.amherst.edu/jsonld/context.json

The port on which the service is made availalbe

    rest.port=9102

The hostname for the service

    rest.host=localhost

The REST prefix

    rest.prefix=/jsonld

By editing this file, any currently running routes will be immediately redeployed
with the new values.

For more help see the [Apache Camel](http://camel.apache.org) documentation

