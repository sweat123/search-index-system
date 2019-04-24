# Reducer 

`Reducer`用来将数据写入目标数据源，`Reducer`有不同的实现。

目前会支持以下数据源

1. `Solr`
2. `ElasticSearch`
3. `Mysql`

![reducer](./pics/reducer-work.PNG)

`Reducer`会尽可能使用批量处理的方式，保证性能和稳定性。
