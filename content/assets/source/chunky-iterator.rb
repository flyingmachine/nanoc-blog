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
