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


# 延迟关联
1. 场景： 
   + 数据库中有几千万数据
   + 符合条件的结果总数较多，比如有1000w
   + 此时需要分页查询3000000，10的数据
   + 已经使用了索引，查询效率还是较低
2. 使用延迟关联查询
    ```
    -- 优化前
    EXPLAIN
    select t.* from user_info t order by t.GMT_CREATE limit 3000000,10;
    
    -- 优化后
    EXPLAIN
    select a.* from user_info a
    INNER JOIN (
    select t.id from user_info t order by t.GMT_CREATE limit 3000000,10
    ) b on a.id = b.id;
    ```
3. 高性能MySQL》也提到了延迟关联如下：
![延迟关联.png](https://s2.ax1x.com/2019/03/25/AtDebR.png)