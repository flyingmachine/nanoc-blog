---
title: Follow Your Followers Using Ruby
created_at: May 20 23:23:00 -0500 2009
kind: article
categories: programming
summary: A quick way to automatically follow people who follow you on twitter.
additional_stylesheets:
  - pygments
---

I have the following in a rake task run every 30 minutes. You'll need jnunemaker's twitter gem.

```ruby
require 'twitter'
# Check out the twitter gem docs for using oauth
httpauth = Twitter::HTTPAuth.new("username", "password")
base = Twitter::Base.new(httpauth)
to_follow_ids = base.follower_ids - base.friend_ids
unavailable_count = 0
to_follow_ids.each do |tfid|
  begin
    base.friendship_create(tfid, true)
  rescue Twitter::General
    # Twitter::General is raised for 403 errors
    # Which occur when you're trying to follow someone who's been banned by twitter
    base.block(tfid)
  rescue Twitter::Unavailable 
    # Wait and try again if twitter's telling you to wait
    sleep 5
    if unavailable_count < 3
      retry
      unavailable_count += 1
    end
  end
end
```
