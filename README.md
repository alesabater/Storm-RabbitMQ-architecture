Storm-RabbitMQ-architecture
===========================

This is a project composed by Storm, RabbitMQ and Django. 

It analyses the different packages that pass through a ethernet interface and determines what protocol the package is using, UDF or TCP. This is done so you can monitor in real-time (that is why storm is used), how many package are traveling through your network. 

UDPTCPANALISIS directory > Contains the java code that analyses and separate the packages based on some logs, then it creates a RabbitMQ productor so the results are send to a rabbitMQ server.

WEBTRAFFICDASHBOARD directory > Contains the code for visualizing the packages in real time.
