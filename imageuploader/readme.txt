

build:
make build

run:
sbt run (dev mode)
make run-release

post url:
http://localhost:9000/v1/images/upload

post body:
{
	"urls": [
		"https://farm3.staticflickr.com/2879/11234651086_681b3c2c00_b_d.jpg",
		"https://farm4.staticflickr.com/3790/11244125445_3c2f32cd83_k_d.jpg"
	]
}

upload:
https://imgbb.com/
https://api.imgbb.com/

curl --location --request POST "https://api.imgbb.com/1/upload?key=aec45697733b86cd6335cd95b03c223b" --form "image=base64xxx"





