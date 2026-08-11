# order-management-system
基于 Spring Boot + Redis 的中小电商订单管理系统，支持秒杀防超卖与订单超时自动关闭
# 项目名称

> 面向中小电商场景的订单管理系统，支持下单、库存扣减、超时自动关单全流程。

## 📷 项目截图
<img width="1280" height="800" alt="屏幕截图 2026-08-11 144625" src="https://github.com/user-attachments/assets/bd40b3ab-7605-4566-9750-53274f408252" />


## ✨ 功能特性
- 核心功能 1：基于 Redis + Lua 的库存扣减，解决并发超卖
- 核心功能 2：延迟队列实现订单超时自动关闭
- 核心功能 3：完整的下单 → 支付回调 → 发货状态机
  
## 🛠 技术栈
- 后端：Spring Boot / MyBatis
- 数据库：MySQL + Redis

## 🚀 快速开始
git clone https://github.com/RuiXuan Wu/轩-711.git
cd 轩-711

## 📌 后续计划
- [ ] 待办事项 1

## 📄 License
MIT
