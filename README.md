### Build for docker

```
./gradlew clean

./gradlew :entities:build -Dquarkus.container-image.build=true
```

copy `entities/build/quarkus-app/` for docker deploy.
