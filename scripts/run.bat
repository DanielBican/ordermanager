@echo on
echo Building ordermanager
call ../gradlew -p ../ build
echo Starting docker-compose services
call docker-compose up -d
