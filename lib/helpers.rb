include Nanoc3::Helpers::Blogging
require 'pp'
require "redcarpet"
require "pygments.rb"

module CustomHelpers
  def format_date(date)
    Time.parse(date).strftime("%d %B %Y")    
  end

  def link_to(text, item, html_class = nil)
    "<a href='#{item.path}' class='#{html_class}'>#{text}</a>"
  end

  def article?(item)
    item[:kind] == 'article'
  end
end

class RedcarpetSyntaxHighlighter < Redcarpet::Render::HTML
  def block_code(code, language)
    "<div class='code pygments'>" + Pygments.highlight(code, :lexer => language) + "</div>"
  end
end

include CustomHelpers
