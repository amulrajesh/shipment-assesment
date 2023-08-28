# Spring Boot + Angular + Kubernetes + Helm

## Prerequisites
```curl
        Eclipse IDE
        Jdk 17
        Spring Boot - 3.1.1
        Maven - 3
        Kubernetes - 1.22.1
        Docker 20.10.8 
        Kubectl (Client 18.09.2 && Server 18.09.2)
        Helm 3
```

## Application Details
```curl
        Aggregation API
            An API (/aggregation) calls the Pricing, Tracking and Shipment APIs and return the aggregated response.
            
       Idea:
       		- Using queues to maintain incoming request and store the aggregate response
       		- It would be nice if we use any messaging server like Kafka or RabbitMQ 
        
        Flow
            - Call Pricing, Tracking and Shipment APIs individually.
            - When calling any API it will wait till queue reaches 5 request or till time reaches 5 seconds - The Queue size and time limit is configured in application.yml file
            - Generating an UID for each request to map the response back with aggregated response
            - Assigning the payload and UID in incomeQueue variable, so later we can filter the aggregate response and write it to an outgoingQueue          
           
```

## Git Clone
```curl
    git clone https://github.com/amulrajesh/shipment-assesment.git
```


## Running Without Helm
```curl
        Greetings API
            1. cd aggregatioin-flux
            
            2. Open Gitbash
            
            3. Run ./runScript.sh <<DOCKER_IMG_VERSION> <<DOCKER_CMD_FLAG>> <<KUBECTL_CMD_FLAG>>
                Example: ./aggregation-api.sh 0.0.1-SNAPHSOT true true
                If DOCKER_CMD_FLAG version is true
                    1. Build docker image
                    2. Push docker image 
                If KUBECTL_CMD_FLAG is true
                    1. Create Kubernetes service
                    2. Replace deployment.yaml image API_VERSION with DOCKER_IMG_VERSION
                    3. Create kubernetes deployment
            
            4. To verify execute "kubectl get all"
                $ kubectl get pod,svc,deploy
                NAME                                READY   STATUS    RESTARTS   AGE
                pod/aggregation-api-788869c-27hjl   1/1     Running   0          26m

                NAME                              TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
                service/aggregation-api-service   NodePort    10.109.114.188   <none>        8080:8080/TCP   26m
                service/kubernetes                ClusterIP   10.96.0.1        <none>        443/TCP          64m

                NAME                              READY   UP-TO-DATE   AVAILABLE   AGE
                deployment.apps/aggregation-api   2/2     2            2           26m

```
