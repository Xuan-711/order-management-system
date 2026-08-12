-- 商品表
DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100)  NOT NULL COMMENT '商品名称',
  stock       INT           NOT NULL DEFAULT 0 COMMENT '库存数量',
  price       DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '单价',
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
DROP TABLE IF EXISTS t_order;
CREATE TABLE t_order (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no      VARCHAR(32)   NOT NULL UNIQUE COMMENT '订单号',
  product_id    BIGINT        NOT NULL COMMENT '商品ID',
  quantity      INT           NOT NULL COMMENT '购买数量',
  amount        DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  status        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态:0待支付 1已支付 2已发货 3已关闭',
  create_time   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  pay_time      TIMESTAMP     NULL,
  ship_time     TIMESTAMP     NULL,
  close_time    TIMESTAMP     NULL
);
