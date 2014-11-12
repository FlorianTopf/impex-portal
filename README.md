impex-portal
============

IMPEx portal for access to scientific Web services
http://impex-portal.oeaw.ac.at

Current Backend libraries used:
- Scala: 2.10.4
- Play: 2.3.5
- Akka: 2.3.4
- Scalaxb: 1.1.2
- Swagger: 1.3.10

Current Frontend libraries used:
- Angular.js: 2.1.14 (incl. various third-party modules)*
- Typescript: 0.9.1.0
- Bootstrap: 3.2.0
- JQuery: 2.1.1

*please see public/js/bower.json for details

Production version backend includes:
- current IMPEx configuration file in XML/JSON format (conf directory)
- offline versions of the XML metadata trees and WSDL files (trees/methods directory)
- default loading of remote resources and initialisation of actor system 
  (registry and data providers) in Global.scala (default package)
- controllers for config/tree exploitations including JSON/XML transformation (controllers package)
- controllers for individual web services of the databases connected to scalaxb SOAP-XML parsers (controllers package)
- actions for filter and userdata services (Application.scala)
- swagger annotations for all relevant REST services (methods and registry)
- actor system with ConfigService.scala, DataProvider.scala 
  (ObsDataProvider.scala/SimDataprovider.scala) and 
  RegistryService.scala (models.actor package)
- extended actor system with UserService.scala, connected to a HTTP-Session (models.actor package)
- all needed XML/JSON bindings for the IMPEx configuration file, SPASE-based data trees, 
  as well as for all WSDL files and the VOTable format (models.binding package)
- auxiliary sources for the scalaxb library (scalaxb / soapenvelope11 package, see: http://scalaxb.org/)
- helper services for file operations, ISO time creation and URI creation (models.provider package)
- root view for the portal map, including all JS includes
- api view using the swagger-ui package to display to swagger api
- all used XML Schemas in bindings (xsd directory)
- specs for all backend components (test directory)

Production version frontend includes: (public directory)
- all included JS libraries (js/libs directory)
- libraries for swagger-ui (lib directory)
- configs for karma/jasmine testing environment (config directory) and related specs (test directory)
- typescript'ed models for config, service responses, spase, swagger and for user related data
- typescript'ed models for angular-ui router states (models/state.ts)
- services (singletons) for distributing data through REST based angular-resource DAOs 
  (ConfigService, MethodsService, RegistryService, SampService, UserService)
- controller for initial state (ConfigCtrl)
- controller for portal map (PortalCtrl)
- controllers for dialogs (MethodsCtrl, RegistryCtrl, UserDataCtrl)
- directives for taking care of DOM updates and manipulation (DatabasesDir, MethodsDir, RegistryDir, SelectionDir, UserDataDir)
- html partials and templates for the directives and ui-router states
- specs for all frontend components (public/js/test directory)
