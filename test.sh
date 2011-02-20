#!/bin/bash

for src in $(ls test/sym/src/*.csv )
do
 tgt=test/sym/dst/$(basename $src .csv).sym
 if java -jar sym.jar -i $src -s $tgt
 then
   continue
 else
   break
 fi
done
