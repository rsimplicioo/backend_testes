build:
	mvn compile

unit-test:
	@echo "executando testes unitários"
	@mvn test

integration-test:
	mvn test -P integration-test

system-test:
	mvn test -P system-test

test: unit-test integration-test

package:
	mvn package

docker-build: package
	docker build -t backend:dev -f ./Dockerfile

docker-start:
	docker compose -f docker-compose.yaml up -d

docker-stop:
	docker compose -f docker-compose.yaml down

start-app:
	mvn spring-boot:start