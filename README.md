#Federated Network Service Engine
A SDN controller software which demonstrates how autoconfiguration can be solved in federate networks. 
It supports Cisco network elements for now. It is develop using Java 1.7, Spring Boot, Hibernate and Maven 3. 


##Installation
There is two projects in this repo the _fnse-core_ and _fnse_. The _fnse_ is dependendt 
of _fnse-core_ so you should build and run the _fnse-core_ before _fnse_. The projectes are built with maven so you can run
`mvn build install`. 

##Run the code
Since the code is only compatible with Cisco I have provided a VIRL configuration file called mthesis-mcat-test.virl so you can launch the application directly in the code.

###Dependencies
The code is dependent of a POSTGRES database named fnse with username zelus and password admin123. This password and username can be changed in the no.mil.fnse.configuration.RootConfig.java.

###Configuration 

The application is dependent upon a configuration file having the format:

```json
{
	"nationalController":{
		"ip":"47.0.0.2",
		"id":"47",
		"interval":30
	},
	"network_elements":[
		{
			"managementIp":"47.0.0.1",
			"username":"cisco",
			"password":"cisco"
		}
	]
}
```
The configuration file should be located at the same location as the .jar file at runtime. 


