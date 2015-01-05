module RuleHelper
  def basename(fname)
    File.basename(fname, File.extname(fname))
  end
end

module Nanoc
  class RuleContext
    include RuleHelper
  end
end

class HTMLwithPygments < Redcarpet::Render::HTML
  def block_code(code, language)
    Pygments.highlight(code, lexer: language)
  end
end

Markdowner = Redcarpet::Markdown.new(HTMLwithPygments, fenced_code_blocks: true)


class MDFilter < Nanoc::Filter
  identifier :pygmented_md
  type :text
  def run(content, params={})
    Markdowner.render(content)
  end
end
