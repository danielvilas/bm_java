# bm_java
Benchmark Java
```sh
sudo apt-get update

sudo apt-get install maven
git clone https://github.com/danielvilas/bm_java
cd bm_java
mvn package

 java -jar full/target/full-1.0-SNAPSHOT-jar-with-dependencies.jar -d 0Initial -p KAFKA
```