.PHONY: gen-version

all:  gen-version

gen-version:
	git describe --tags --always
	git describe --tags --always | sed 's/v/ /g' | sed 's/\./ /g' | sed 's/-/ /g' | awk '{print ($$1*16777216)+($$2*65536)+($$3*256)+$$4}'

#make gen v=v1.1.3
gen:
	echo $(v) | sed 's/v/ /g' | sed 's/\./ /g' | sed 's/-/ /g' | awk '{print "{\"version_code\": " ($$1*16777216)+($$2*65536)+($$3*256)+$$4 ", \"version_name\": \"" "$(v)" "\"}"}' > version.json

channels:
	gua64 -f -e files/channels.json -o app/src/main/res/raw/channels.txt