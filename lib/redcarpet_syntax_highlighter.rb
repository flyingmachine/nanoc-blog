require "redcarpet"
require "pygments.rb"

  
class RedcarpetSyntaxHighlighter < Redcarpet::Render::HTML
  def block_code(code, language)
    "<div class='code pygments'>" + Pygments.highlight(code, :options => {:encoding => 'utf-8', :lexer => language}) + "</div>"
  end
end

