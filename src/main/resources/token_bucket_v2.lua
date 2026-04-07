-- Token Bucket Algorithm
-- KEYS[1] = clientKey
-- ARGV[1] = maxTokens
-- ARGV[2] = refillRate (tokens per second)

local clientKey = KEYS[1]
local maxTokens = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])

local timeArray = redis.call('TIME')
local currentTime = tonumber(timeArray[1])

local cjson = require("cjson")
local jsonString = redis.call('GET', clientKey)

-- New client: create bucket with full tokens minus 1 for this request
if jsonString == false then
    local newTokens = maxTokens - 1
    local data = '{"lastRefill":' .. currentTime .. ',"tokens":' .. newTokens .. '}'
    redis.call('SET', clientKey, data)
    return {1, currentTime + 1, newTokens}
end

local data = cjson.decode(jsonString)

local lastRefill = tonumber(data.lastRefill)
local tokens = tonumber(data.tokens)

-- Calculate tokens to add based on time passed
local timePassed = currentTime - lastRefill
local tokensToAdd = timePassed * refillRate
local currentTokens = math.min(maxTokens, tokens + tokensToAdd)

-- Not enough tokens: reject
if currentTokens < 1 then
    local retryAfter = math.ceil((1 - currentTokens) / refillRate)
    return {0, currentTime + retryAfter, 0}
end

-- Consume 1 token
local newTokens = currentTokens - 1
local newData = '{"lastRefill":' .. currentTime .. ',"tokens":' .. newTokens .. '}'
redis.call('SET', clientKey, newData)

return {1, currentTime + 1, newTokens}