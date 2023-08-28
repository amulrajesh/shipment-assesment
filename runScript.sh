#!/bin/bash

if [ $# -le 2 ]
  then
    echo "No/Less than 3 arguments are supplied"
    exit 1
fi

APP_VERSION=$1


if [ $2 = true ]
    then
        docker build -t amulrajesh/aggregation-api:${APP_VERSION} .

        docker push amulrajesh/aggregation-api:${APP_VERSION}
fi

if [ $3 = true ]
    then
        kubectl apply -f deployment/service.yaml --validate=false

        sed "s/APP_VERSION/${APP_VERSION}/g" \
            deployment/deployment.yaml \
            > deployment/deployment_current.yaml

        kubectl apply -f deployment/deployment_current.yaml --validate=false
fi