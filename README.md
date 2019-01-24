# Onboard Assistant

Slack automated assistant to onboard new staff members


These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

```
git clone https://github.com/cgi-digital/onboard-assistant.git
```

What things you need to install the software and how to install them

```
Git 
Java JDK 8+ (GraalVM) 
MongoDB 
ngrok 
```


A step by step series of examples that tell you how to get a development env running

Say what the step will be

1.Get the API token from slack for the onboard assistant and copy to the application.yml bot token
2. Install ngrok and add to the path
```
spring:
  data:
    mongodb:
      uri: "${MONGO_HOST:mongodb://localhost:27017}"
slack:
  auth:
    token: "${OAUTH_TOKEN}"
```

Start ngrok at the command line, make sure that you're not running with any proxies listed

```
ngrok http PORT
```
Create a new channel in slack and point both the slack application to that channel.

To deploy, create the docker image
```
Run the dockerfile 

push to registry 

within deployment server pull image and run with following 
docker run (-d) -p 9000:9000 --name onboard-assistant 
-e MONGO_HOST="MONGO_URL" 
-e OAUTH_TOKEN="OAUTH_TOKEN" registry_url/onboard-assistant

(-d) if you want to run as a daemon process
```

Add additional notes about how to deploy this on a live system

Hat Tip 

* [jslack](https://github.com/seratch/jslack)
* [micronaut](http://micronaut.io/)