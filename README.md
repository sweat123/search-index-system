[![Build Status](https://travis-ci.org/sweat123/search-index-system.svg?branch=master)](https://travis-ci.org/sweat123/search-index-system.svg?branch=master)


# index system base on kafka connect

## 使用前必看

项目能够运行的前提条件:

1. `kafka`版本`1.0.0`;
2. `kafka topic`数据需要来自`debezium`，简单的理解: 强依赖`debezium`;

## 文档

[文档地址](docs/目录.md)

## 特点

1. 基于`kafka connect`，天然支持高可用。
2. `kafka connect`完备的`commit`处理。
3. 支持数据库反查，聚合数据。
4. 支持写入`solr`，开发者也可以实现自己的写入端。
5. 支持设置`task`个数，并行处理（并行个数与`kafka topic partition`个数相关）。

## TODO

- [x] `Executor`每条`sql`支持不同的`dataSource`。
- [x] 通过`docker`构建环境，使用`Java`代码运行`sis`。
- [x] 支持写入`ES`。
- [ ] 支持写入`Mysql`。
- [x] 增加集成测试。

## Any Problem

`email`: `sweatott@gmail.com`


# 其它

欢迎各位`PR`. `To be better`
