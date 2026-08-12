-- Redis Lua 脚本：原子扣减库存
-- KEYS[1] = 库存key, 例如 stock:product:1
-- ARGV[1] = 扣减数量
-- 返回: 1=成功  0=库存不足

local stock = tonumber(redis.call('GET', KEYS[1]))
if stock == nil then
    return 0
end
if stock >= tonumber(ARGV[1]) then
    redis.call('DECRBY', KEYS[1], ARGV[1])
    return 1
else
    return 0
end
