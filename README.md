impex-portal
============

IMPEx portal for access to scientific Web services

Current library versions used:
- Scala: 2.10.3
- Play: 2.2.1
- Akka: 2.2.0

Prototype version includes:
- current IMPEx configuration file in XML format (conf directory)
- offline versions of the XML metadata trees (trees directory)
- default loading of remote resources and initialisation of actor system in 
  Global.scala (default package)
- development controller for tree exploitations in Application.scala (controllers package)
- initial actor system with ConfigService.scala, DataProvider.scala and 
  RegistryService.scala (models.actor package)
- initial bindings for the IMPEx configuration file, observational and 
  simulation data trees (models.binding package)
- auxiliary sources for the scalaxb library (scalaxb package, see: http://scalaxb.org/)
- helper services (models.provider pacage)
- development views enabled with the inbuilt Play template engine (views directory)
- all used XML Schemas in bindings (xsd directory)
- preliminary specs for all components (test directory)
