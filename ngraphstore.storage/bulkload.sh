#!/bin/sh
mvn exec:java -Dexec.mainClass="com.oppsci.ngraphstore.storage.lucene.LuceneBulkLoader" -Dexec.args="$*"

