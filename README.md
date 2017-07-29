调研阶段对sparkml源代码进行修改部分

目前修改的class有DTStatsAggregator,GradientBoostedTrees,Impurities,Variance4GBDT

构建命令：
./build/mvn -Pyarn -Phadoop-2.7 -Dhadoop.version=2.7.3 -Phive -Phive-thriftserver -DskipTests package

更改源代码后需要替换spark 对应的jar(替换前应先备份),执行
cp mllib/target/spark-mllib_2.11-2.0.2.jar /usr/hdp/2.5.3.0-37/spark2/jars/spark-mllib_2.11-2.0.0.2.5.3.0-37.jar
cp mllib-local/target/spark-mllib-local_2.11-2.0.2.jar /usr/hdp/2.5.3.0-37/spark2/jars/spark-mllib-local_2.11-2.0.0.2.5.3.0-37.jar
hadoop fs -rm /hdp/apps/2.5.3.0-37/spark2/spark2-hdp-yarn-archive.tar.gz

然后执行相应的spark任务即可
