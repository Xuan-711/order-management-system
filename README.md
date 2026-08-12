# order-management-system

基于 Spring Boot + Redis 的中小电商订单管理系统，支持秒杀防超卖与订单超时自动关闭

> 面向中小电商场景的订单管理系统，支持下单、库存扣减、超时自动关单全流程。

## ✨ 功能特性

- **秒杀防超卖**：基于 Redis + Lua 原子扣减库存，解决并发超卖；Redis 不可用时自动降级到数据库乐观锁
- **订单超时自动关闭**：基于延迟队列（DelayQueue）实现，30 秒未支付自动关单并回补库存
- **完整状态机**：下单(待支付) → 支付回调(已支付) → 发货(已发货)；超时则自动关闭

## 🛠 技术栈

| 层 | 技术 |
|----|------|
| 后端框架 | Spring Boot 2.7 |
| ORM | MyBatis |
| 数据库 | H2 内存库（默认零安装）/ MySQL（生产） |
| 缓存 | Redis（库存扣减，可选，自动降级） |

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/Xuan-711/order-management-system.git
cd order-management-system
```

### 2. 运行

默认使用 H2 内存数据库，**无需安装 MySQL / Redis 即可启动**。

```bash
# 方式一：Maven 直接运行
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar target/order-management-system-1.0.0.jar
```

启动后：
- 接口地址：http://localhost:8080
- H2 控制台：http://localhost:8080/h2 （JDBC URL 填 `jdbc:h2:mem:order_db`）

### 3. 接口测试

```bash
# 下单（秒杀商品，库存100）
curl -X POST "http://localhost:8080/order/create?productId=1&qty=1"

# 支付（替换 orderNo）
curl -X POST "http://localhost:8080/order/pay?orderNo=xxxxx"

# 发货
curl -X POST "http://localhost:8080/order/ship?orderNo=xxxxx"

# 查看全部订单
curl "http://localhost:8080/order/list"
```

> ⏱ 若下单后 30 秒内未支付，订单将被延迟队列自动关闭并回补库存。

### 4. 切换生产环境（MySQL + Redis）

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/order_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379
```

## 📁 项目结构

```
src/main/
├── java/com/xuan/order/
│   ├── OrderManagementApplication.java   # 启动类
│   ├── common/                           # 枚举、统一返回
│   ├── entity/                           # Product、Order 实体
│   ├── mapper/                           # MyBatis Mapper 接口
│   ├── service/                          # 业务层（含 StockService 库存扣减）
│   ├── controller/                       # REST 接口
│   └── mq/                               # 延迟队列（超时关单）
└── resources/
    ├── application.yml                    # 配置
    ├── schema.sql / data.sql             # 建表与初始数据
    ├── mapper/*.xml                      # MyBatis SQL
    └── lua/deduct_stock.lua              # Redis 原子扣库存脚本
```

## 📌 后续计划

- [ ] 接入 RocketMQ 延迟消息替代本地 DelayQueue
- [ ] 增加幂等性控制（防止支付回调重复处理）
- [ ] 分布式锁优化多节点扣库存

## 📄 License

MIT
