#!/bin/bash

function testSym {
    for src in $(ls test/sym/src/*.csv )
    do
	tgt=test/sym/dst/$(basename $src .csv).sym
	if java -jar sym.jar -i $src -s $tgt
	then
	    continue
	else
	    return 1
	fi
    done
    return 0
}

function testSch {
    #
    # Perform quick and dirty edit on sheet (with sed and grep) for testing purposes
    #
    if java -jar sch.jar -p ADXL345 HMC5834 -l test/sym/src | sed 's/ .*//' | egrep -v ',(NC|RESERVED)$' > test/sch/src/schematic-0.csv
    then
	java -jar sch.jar -i test/sch/src/schematic-0.csv -l test/sym/src -s test/sch/dst/schematic-0.sch
    else
	return 1
    fi
}

set -x

if testSym
then
    testSch
else
    exit 1
fi
