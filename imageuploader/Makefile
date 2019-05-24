
build:
	sbt assembly
	docker build -t image-uploader-service .


run-release:
	docker run --rm --name image-uploader -p 9000:9000 image-uploader-service



