#!/bin/bash

cp mllib/target/spark-mllib_2.11-2.0.2.jar /usr/hdp/2.5.3.0-37/spark2/jars/spark-mllib_2.11-2.0.0.2.5.3.0-37.jar
cp mllib-local/target/spark-mllib-local_2.11-2.0.2.jar /usr/hdp/2.5.3.0-37/spark2/jars/spark-mllib-local_2.11-2.0.0.2.5.3.0-37.jar
hadoop fs -rm /hdp/apps/2.5.3.0-37/spark2/spark2-hdp-yarn-archive.tar.gz
