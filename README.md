# Shopping-List
This is a showcase of the Java-Framework Spring Boot in Combination with the Typescript-Frontend-Framework Angular with PrimeNG.
The Shopping-List can be started with [Docker](docker-compose.yml).

## Links:
* Running Version: http://shopping-list.app-node.de/
* Backend: https://github.com/BlueIronGirl/shopping-list-backend
* Frontend: https://github.com/BlueIronGirl/shopping-list-frontend

## How to run:

### Docker

#### Clean up old containers

```shell
docker-compose rm -f db
docker-compose rm -f backend
docker-compose rm -f frontend
```

#### Reset Database
```shell
docker volume rm -f shopping-list-app_database
```

#### Start containers with Docker-Compose
```shell
docker-compose up
```

