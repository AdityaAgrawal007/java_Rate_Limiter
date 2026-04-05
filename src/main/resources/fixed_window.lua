-- store client info - startTimeStamp, count
-- requires clientID, token limit, Time window
-- check if client allowed to request or not
-- return RateLimitResult obj

-- if we use Hash datatype to store the client info then we need to make two HGET calls to retrive both feilds but
-- if we instead use JSON string datatype we only need one GET & SET call to perform operatins

local clientKey = tostring(KEYS[1])
local tokenLimit = tonumber(ARGV[1])
local timeWindow = tonumber(ARGV[2])
local timeArray = redis.call('TIME')
local currentTime = tonumber(timeArray[1])

-- check if key exists, if not make it and return stuff
if redis.call('EXISTS', clientKey) == 0 then
    -- set startTime to when he requested
    -- and count to 1

    redis.call('SET', clientKey, '{"startTimestamp": ' .. currentTime .. ' , "count":1}')
    local remainingTime = (currentTime + timeWindow)
    local remainingTokens = (tokenLimit - 1)

    return { 1, remainingTime, remainingTokens }
end

-- extract client details via cjson library

-- local cjson = require("cjson")

local jsonString = redis.call('GET', clientKey)

local data = cjson.decode(jsonString)


local startTimestamp = tonumber(data.startTimestamp)
local count = tonumber(data.count)

local newTimestamp = startTimestamp + timeWindow

-- if user requests after time Window
if newTimestamp <= currentTime then
    redis.call('SET', clientKey, '{"startTimestamp": ' .. currentTime .. ', "count": 1}')

    local remainingTime = (currentTime + timeWindow)
    local remainingTokens = (tokenLimit - 1)

    return { 1, remainingTime, remainingTokens }
else
    -- if user requests within the time
    local resetTimestamp = startTimestamp + timeWindow
    local remainingTime = (resetTimestamp - currentTime)

    -- if he is out of tokens
    if (count + 1) > tokenLimit then
        return { 0, remainingTime, 0 }

    -- if he has got enough tokens
    else
        redis.call('SET', clientKey, '{"startTimestamp":' .. startTimestamp .. ',"count":' .. (count + 1) .. '}')
        return {1, remainingTime, (tokenLimit - count - 1)}
    end
end