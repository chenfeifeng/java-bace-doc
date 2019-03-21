# 新增或者修改
1. 使用ON DUPLICATE KEY UPDATE函数
2. 插入成功返回修改条数行数1，修改成功返回条数为2
3. 使用方法
   + 创建表
   ```
   CREATE TABLE `test` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT,
     `name` varchar(255) DEFAULT NULL,
     `age` bigint(40) DEFAULT NULL,
     `sex` int(2) DEFAULT NULL,
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_name` (`name`)
   ) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4
   ```
   + 此表id自增，name唯一
   + 新增数据，当name一样时，进行修改
   ```
   insert test_cff(name, age, sex) VALUES ('ccc',1111,222222)
   ON DUPLICATE KEY UPDATE
   age=VALUES(age),sex=values(sex);
   ```
---