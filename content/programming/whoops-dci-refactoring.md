---
title: A Detailed Look at a Small DCI Refactoring in Ruby
created_at: Nov 30 10:12:00 -0500 2012
kind: article
categories: programming
summary: "In this post I go over a small refactoring to clean up some code in <a href='http://www.whoopsapp.com/'>Whoops</a> by implementing the DCI pattern. I'll cover the actual code changes and include my usual hand-wringing about what could be done better."
additional_stylesheets:
  - pygments
---

UPDATE: this isn't actually DCI! Whoops!

In this post I go over a small refactoring to clean up some code in
[Whoops](http://www.whoopsapp.com/) by implementing the DCI
pattern. I'll cover the actual code changes and include my usual
hand-wringing about what could be done better.

This refactoring was inspired by Jim Gay's book
[Clean Ruby](http://clean-ruby.com/). Jim Does an excellent job of
concisely explaining what the DCI pattern (data, context, interaction)
is, why it's useful, and how you can implement it in Ruby. This post
isn't a review of Clean Ruby, but I will say that it's worth the
money - it's actually rekindled my enthusiasm for Ruby!

## Background

Whoops is a free, open-source Rails engine for logging. It's similar
to Airbrake, Errbit, and [Exceptional](http://www.exceptional.io/)
except that it's not limited to exceptions - you can log any event.

##The Original Code and Its Defects

The code that got refactored is concerned with processing new
events. You can see the entry point to this use case at lines 18-28
here:

```ruby
class Whoops::Event
  include Mongoid::Document
  include FieldNames
  
  belongs_to :event_group, :class_name => "Whoops::EventGroup", :index=>true
  
  field :details
  field :keywords, :type => String
  field :message, :type => String
  field :event_time, :type => DateTime

  index([[:event_group_id,Mongo::ASCENDING],[:event_time, Mongo::DESCENDING]])

  validates_presence_of :message  
  
  before_save :set_keywords, :sanitize_details
  
  def self.record(params)
    params = params.with_indifferent_access
    
    event_group_params                    = params.slice(*Whoops::EventGroup.field_names)
    event_group_params[:last_recorded_at] = params[:event_time]
    event_group_params
    event_group = Whoops::EventGroup.handle_new_event(event_group_params)
    
    event_params = params.slice(*Whoops::Event.field_names)
    event_group.events.create(event_params)
  end 
  
  def self.search(query)
    conditions = Whoops::MongoidSearchParser.new(query).conditions
    where(conditions)
  end
  
  def set_keywords
    keywords_array = []
    keywords_array << self.message
    add_details_to_keywords(keywords_array)
    self.keywords = keywords_array.join(" ")
  end
  
  def sanitize_details
    if details.is_a? Hash
      sanitized_details = {}
      details.each do |key, value|
        if key =~ /\./
          key = key.gsub(/\./, "_")
        end
        
        if value.is_a? Hash
          child_keys = all_keys([value])
          if child_keys.any?{ |child_key| child_key =~ /\./ } 
            value = value.to_s
          end
        end
        
        sanitized_details[key] = value
      end
      
      self.details = sanitized_details
    end
  end
  
  def all_keys(values)
    keys = []
    values.each do |value|
      if value.is_a? Hash
        keys |= value.keys
        keys |= all_keys(value.values)
      end
    end
    keys
  end
    
  private

  
  def add_details_to_keywords(keywords_array)
    flattened = details.to_a.flatten
    flattened -= details.keys if details.respond_to?(:keys)
    
    until flattened.empty?
      non_hash = flattened.select{ |i| !i.is_a?(Hash) }
      keywords_array.replace(keywords_array | non_hash)
      flattened -= non_hash
      
      flattened.collect! do |i|
        i.to_a.flatten - i.keys
      end.flatten!
    end
  end
end
```

The above `record` method calls `handle_new_event` in
`Whoops::EventGroup` (lines 21-41):

```ruby
class Whoops::EventGroup
  # notifier responsible for creating identifier from notice details
  include Mongoid::Document
  include FieldNames
  
  [
    :service,
    :environment,
    :event_type,
    :message,
    :event_group_identifier,
    :logging_strategy_name
  ].each do |string_field|
    field string_field, :type => String
  end
  
  field :last_recorded_at, :type => DateTime
  field :archived, :type => Boolean, :default => false
  field :event_count, :type => Integer, :default => 0

  class << self
    def handle_new_event(params)
      identifying_params = params.slice(*Whoops::EventGroup.identifying_fields)
      event_group = Whoops::EventGroup.first(:conditions => identifying_params)
      
      if event_group
        event_group.attributes = params
      else
        event_group = Whoops::EventGroup.new(params)
      end
      
      if event_group.valid?
        event_group.send_notifications
        event_group.archived = false
        event_group.event_count += 1
        event_group.save
      end

      event_group
    end
  end
  
  has_many :events, :class_name => "Whoops::Event"
  
  validates_presence_of :event_group_identifier, :event_type, :service, :message
  
  def self.identifying_fields
    field_names - ["message", "last_recorded_at"]
  end
  
  # @return sorted set of all applicable namespaces
  def self.services
    all.distinct(:service).sort
  end

  def should_send_notifications?
    (archived || new_record) && Rails.application.config.whoops_sender
  end
  
  def send_notifications
    return unless should_send_notifications?
    matcher = Whoops::NotificationSubscription::Matcher.new(self)
    Whoops::NotificationMailer.event_notification(self, matcher.matching_emails).deliver unless matcher.matching_emails.empty?
  end
end
```

I've included the full source of the files to show why this code
doesn't belong. See, one of the main ideas in Jim's book is that code
becomes unmaintainable when we start overburdening classes with
methods that are only needed in specific contexts, or use
cases. Additionally, we scatter all the code needed in one context
across multiple classes and files rather than keeping it all in one
place, creating a cognitive burden.

### Violating The Single Responsibility Principle

In this case, the context is "record an event." The code we've added to the `Whoops::Event` class begins to overburden the class by giving it a new responsibility and requiring that it "know" more about the outside world. Here's what the class is already responsible for, along with what we're adding:

* CRUD events
* Massage event data before persistence
* _NEW_ Prepare event group data (lines 21-23)
* _NEW_ Initiate event group handling of new event (line 24)
* _NEW_ Figure out how to separate event-relevant data from event params (line 27)

In this case, the impact isn't that high because we still don't even
have 100 LOC for the entire class. But by continuing to shove code
into a class just because a class's data or existing behavior is
somehow involved, we transform our classes from usable, focused
abstractions into code warehouses. And I really do mean warehouse. You
have to start using signage in the form of comments or mixins to find
your way around. "Aaah yes, here we are, the email notification
section!" or "Oh shit! it looks like someone put the methods for
handling user registration at different ends of the file. Let's put
those two guys together. There, what a tidy warehouse!"

The `Whoops::EventGroup` class was becoming a code warehouse. Here are
the new responsibilities brought on by `handle_new_event`

* CRUD event groups
* validate event groups
* _NEW_ send a notification on new events
* _NEW_ figure out whether a notification should be sent (lines 56-58)

This notification code is most definitely not the responsibility of a
`Whoops::EventGroup`. But here it is, because hey, fat model skinny
controller.

### Spreading Out Related Code

The above two files also show how related code gets spread out. In
this case, to understand the full behavior of "handle new event" you
have to start in the `Whoops::Event` file, then go to the
`Whoops::EventGroup` file, then return back to the `Whoops::Event`
file.

### Burdening Your Mind Grapes

(For more on mind grapes, see #6 in [this cracked
article](http://www.cracked.com/article_15283_the-10-best-moments-from-3Cem3E30-rock3Cem3E.html))

As anyone who's dealt with this kind of code can attest, organizing
your code this way adds cognitive load. By violating the single
responsibility principle, we make it more difficult for ourselves to
find the methods we're interested in. The class's name starts to lose
its meaning as its conceptual category becomes broader and broader
until it becomes useless as an organizational unit. It's like trying
to find the latest teen vampire romance novel at a bookstore only to
find it in the computer section because the sexy, misunderstood male
vampire love interest has an iphone.

And everyone knows that having to navigate multiple files in order to
understand one use case is a huge pain in the ass. I mean, that's
pretty much why Chris Granger is making
[Light Table](http://www.lighttable.com/), right?

## The Refactoring

[This github diff](https://github.com/flyingmachine/whoops/compare/abc6d496024272e5a596d280bd47c51ba3a3d953...b6943881ddad094543d80528df79cc450e516c9f)
shows all the changes to DCI-ify the code.

As you can see, our `Whoops::Event` and `Whoops::EventGroup` classes are no longer violating the single responsibility principle. Now when you dig into those classes you no longer have to wade through code that's only tangentially related to the real purpose of those closses. We're no longer burdening our classes with knowledge that they shouldn't have, reducing coupling.

Additionally, all of the code related to handling a new event is now
in one place, the `Whoops::NewEvent` class. This makes it much easier
to understand completely everything that happens when a new event is
handled. Rejoice!

I'm not sure how much more to say about the benefits of this
refactoring. It feels a lot nicer to me, and it un-does the problems
that existed earlier. Hopefully that's evident, but if it's not then
please leave a comment or [tweet me](http://twitter.com/nonrecursive!)

## The Promised Hand-Wringing

There are still some things that I'm not completely happy with in
`Whoops::NewEvent`.

First, it basically looks one gigantic method that's been split up
into tiny methods just so that I could name groups of related code. I
don't know that there's much value in that.

Second, lines 13-15 look like they need some sort of explanation. It's
especially not obvious why I'm setting archived to false.

Last, I'm not completely happy with the name NewEvent. If you were new
to the project you'd probably ask yourself, "Uh... how is that not
Whoops::Event.new?" I also considered "Whoops::NewEventHandler" but I
was afraid that would summon Steve Yegge and he'd make me read one of
his 10,000 word essays.

I'd love to get some thoughts on this! Whether or not this is a
reasonable DCI refactoring, I think that DCI is a great tool and
something OOP folks should investigate.
