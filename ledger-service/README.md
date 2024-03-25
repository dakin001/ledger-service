
# Important!  
design documents in root folder [README.md](../README.md), please do not miss it.  

# Getting Started

## Method 1 - start without dependency 
1. start application
```shell
./gradlew build
java -Dspring.profiles.active= -jar build/libs/app-server.jar 
```

2. open swagger-ui
> [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)

## Method 2 - start with dependency (optional)
1. install Docker Desktop
> https://www.docker.com/products/docker-desktop/

2. start local third party dependence components
```shell
cd scripts 
docker-compose up -d
```

3. start application
```shell
./gradlew build
java -Dspring.profiles.active=local -jar build/libs/app-server.jar 
```


## deployment (not test yet, need add dependencies)
precondition  
`./gradlew build`

1. build docker image
   in project root folder
```shell
docker build -t ledger/service:v1 .
```

2. deployment in kubernetes
```shell
kubectl apply -f scripts/k8s/deployment.yaml
kubectl apply -f scripts/k8s/service.yaml
```
