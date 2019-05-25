
build:
	sbt assemblyPackageDependency
	sbt assembly

run-local:
	java -Dplay.http.secret.key=price -Dhttp.port=9000 -cp "target/scala-2.11/*" play.core.server.ProdServerStart

docker:
	docker build -t image-uploader-service .

run-release:
	docker run --rm --name image-uploader -p 9000:9000 image-uploader-service



