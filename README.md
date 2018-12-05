# index system base on kafka connect

## 特点

1. 基于`kafka connect`，天然支持高可用。
2. `kafka connect`完备的`commit`处理。
3. 支持数据库反查，聚合数据。
4. 支持写入`solr`，开发者也可以实现自己的写入端。
5. 支持设置`task`个数，并行处理（并行个数与`kafka topic partition`个数相关）。

## TODO

- [] `Executor`每条`sql`支持不同的`dataSource`;
- [] 支持写入`ES`。
- [] 支持写入`Mysql`。

## Any Problem

`email`: `sweatott@gmail.com`


# 其它

欢迎各位`PR`. `To be better`
