-- Token Bucket with Hash (no cjson, no require)
local clientKey = KEYS[1]
local maxTokens = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])

local timeArray = redis.call('TIME')
local currentTime = tonumber(timeArray[1])

if redis.call('EXISTS', clientKey) == 0 then
    local newTokens = maxTokens - 1
    redis.call('HMSET', clientKey, 'lastRefill', currentTime, 'tokens', newTokens)
    return {1, currentTime + 1, newTokens}
end

local lastRefill = tonumber(redis.call('HGET', clientKey, 'lastRefill'))
local tokens = tonumber(redis.call('HGET', clientKey, 'tokens'))

if lastRefill == nil or tokens == nil then
    local newTokens = maxTokens - 1
    redis.call('DEL', clientKey)
    redis.call('HMSET', clientKey, 'lastRefill', currentTime, 'tokens', newTokens)
    return {1, currentTime + 1, newTokens}
end

local timePassed = currentTime - lastRefill
local tokensToAdd = timePassed * refillRate
local currentTokens = math.min(maxTokens, tokens + tokensToAdd)

if currentTokens < 1 then
    local retryAfter = math.ceil((1 - currentTokens) / refillRate)
    return {0, currentTime + retryAfter, 0}
end

local newTokens = currentTokens - 1
redis.call('HMSET', clientKey, 'lastRefill', currentTime, 'tokens', newTokens)

return {1, currentTime + 1, newTokens}
