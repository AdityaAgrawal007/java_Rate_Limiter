local clientKey = KEYS[1]
local limit = tonumber(ARGV[1])
local windowSeconds = tonumber(ARGV[2])

local timeArray = redis.call('TIME')
local currentTime = tonumber(timeArray[1])

if redis.call('EXISTS', clientKey) == 0 then
    local remaining = limit - 1
    redis.call('SET', clientKey, 1, 'EX', windowSeconds)
    return {1, currentTime + windowSeconds, remaining}
end

local count = tonumber(redis.call('GET', clientKey))
local ttl = tonumber(redis.call('TTL', clientKey))

if count == nil or ttl == nil or ttl < 0 then
    local remaining = limit - 1
    redis.call('SET', clientKey, 1, 'EX', windowSeconds)
    return {1, currentTime + windowSeconds, remaining}
end

if count >= limit then
    return {0, currentTime + ttl, 0}
end

count = redis.call('INCR', clientKey)
ttl = tonumber(redis.call('TTL', clientKey))
local remaining = limit - count

return {1, currentTime + ttl, remaining}
