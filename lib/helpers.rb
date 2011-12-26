include Nanoc3::Helpers::Blogging
require 'pp'

module CustomHelpers
  def format_date(date)
    Time.parse(date).strftime("%d %B %Y")    
  end

  def read_more(article)
    
  end

  def link_to(text, item, html_class = nil)
    "<a href='#{item.path}' class='#{html_class}'>#{text}</a>"
  end
end

include CustomHelpers
