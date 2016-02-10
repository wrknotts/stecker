from stecker.pygments import SyntaxHighlighter
from pygments import highlight
from pygments.lexers import get_all_lexers
from pygments.lexers import get_lexer_by_name
from pygments.lexers import guess_lexer
from pygments.lexers import guess_lexer_for_filename
from pygments.formatters import HtmlFormatter

class PygmentsSyntaxHighlighter(SyntaxHighlighter, object):
    ''' Pygments-based SyntaxHighlighter '''

    def __init__(self):
        pass

    def highlightCode(self, code, type, lineNbrsToHighlight, escapeNewlinesForJavaScript):
        ''' This function is an implementation of a method defined by the SyntaxHighlighter
            Java interface. In this function, the first parameter is a reference to the
            called instance, the second is the code to be formatted,the third is to control
            line number highlighting and the fourth determines if highlighted code should be
            escaped for use with JavaScript. '''
        if escapeNewlinesForJavaScript:
            return highlight(code, get_lexer_by_name(type), HtmlFormatter(lineseparator='\\n', cssclass="stecker", hl_lines=lineNbrsToHighlight, lineanchors="line"))
        else:
            return highlight(code, get_lexer_by_name(type), HtmlFormatter(cssclass="stecker",  hl_lines=lineNbrsToHighlight, lineanchors="line"))

    def getLexerInfo(self):
        ''' This function is an implementation of a method defined by the SyntaxHighlighter
            Java interface. In this function, the first parameter is to be a reference to the
            called instance. '''
        info = []
        for fullname, names, exts, _ in get_all_lexers():
            tup = (fullname, ','.join(names),
                   ','.join(exts) or '')
            info.append(tup)
        return info

    def guessLexerForFilename(self, filename, code):
        return getattr(guess_lexer_for_filename(filename, code), "aliases")

    def guessLexer(self, code):
        return getattr(guess_lexer(code), "aliases")
