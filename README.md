# spring-controller-tests

## Run
```
./gradlew bootRun
```

## Example calls
```
# no context attributes required
curl "http://localhost:8080?context=%7B%22value%22:%20123%7D"

# provide required context attributes: value1, value2
curl -H "context-attributes: value1, value2" "http://localhost:8080/?context=%7B%20%22value1%22%3A%20123%2C%20%22value2%22%3A%20456%20%7D"

# fail to to provide required context attributes: value1, value2
curl -H "context-attributes: value1, value2" "http://localhost:8080/?context=%7B%20%22value0%22%3A%20123%2C%20%22value2%22%3A%20456%20%7D"
```

## OpenApi group
see: GroupedOpenApi @Bean at https://springdoc.org/#migrating-from-springfox
