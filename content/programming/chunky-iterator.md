---
title: "Chunky Iterator: So You Don't have to Load All your AR Objects at Once"
created_at: Mon, 12 May 2008 22:10:50 +0000
kind: article
categories: programming
summary: "The following code lets you iterate over large collections of Active Record objects without having to load them all at once, thus reducing memory usage. It's allowed me to run cron jobs which iterate over thousands of records without getting the cron'd process killed for using too much of a system's resources."
additional_stylesheets:
  - pygments
---

The following code lets you iterate over large collections of Active
Record objects without having to load them all at once, thus reducing
memory usage. It's allowed me to run cron jobs which iterate over
thousands of records without getting the cron'd process killed for
using too much of a system's resources.

```ruby
class ChunkyIterator
  include Enumerable
  def initialize(model_class, chunk_size, options)
    @model_class = model_class
    @chunk_size = chunk_size
    @options = options
  end

  def each
    rows = @model_class.find(:all, merged_options(0))

    until model_objects.empty?
      rows.each{|record| yield record}
      model_objects = @model_class.find(:all, merged_options(rows.last.id))
    end
  end

  def merged_options(id)
    @options.merge(
      :conditions => merge_conditions("#{@model_class.table_name}.id > #{id}"),
      :limit => @chunk_size
    )
  end

  def merge_conditions(added_condition)
    existing_condition = @options[:conditions]
    new_condition = case existing_condition
    when nil: added_condition
    when String: "(#{existing_condition}) AND (#{added_condition})"
    when Array
      ["(#{existing_condition[0]})" +
       " AND (#{added_condition})"] +
       existing_condition[1..-1]
    end
  end
end

# Example
Bacon.find_all_in_chunks(500, :conditions => "fresh = TRUE").each do |bacon|
  bacon.feed_to_cat
end
```

* Update: altered code to use ID rather than offset, like Jamis Buck does.
* Update 2: Fixed merge_conditions per Frank's observation
