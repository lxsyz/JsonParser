package reader;

import exception.ParseException;
import model.Token;
import model.TokenType;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Administrator on 2017/6/8.
 */
public class TokenReader {

    private String filePath;
    // 记录读取到哪一个位置
    private int pos;
    // 总的字符串的长度
    private int length;
    private char[] buffer = new char[1024];
    private FileReader reader;
    private int lineNum = 1;    //行号
    private int position = 1;   //列号
//    private BufferedReader br;

    public TokenReader(String filePath) {
        this.filePath = filePath;
        try {
            reader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Token getToken() throws IOException {
        while (!isEnd()) {
            char ch = '?';
            if (pos < length) {
                ch = buffer[pos];
            }
            if (!isSpace(ch)) {
                switch (ch) {
                    case '\"':
                        return new Token(TokenType.STRING, getString(), lineNum, position);
                    case ',':
                        nextChar();
                        return new Token(TokenType.SEP_COMMA, ',', lineNum, position);
                    case '-':
                        return new Token(TokenType.NUMBER, getNumber(), lineNum, position);
                    case ':':
                        nextChar();
                        return new Token(TokenType.SEP_COLON, ':', lineNum, position);
                    case '[':
                        nextChar();
                        return new Token(TokenType.BEGIN_ARRAY, '[', lineNum, position);
                    case ']':
                        nextChar();
                        return new Token(TokenType.END_ARRAY, ']', lineNum, position);
                    case 'f':
                    case 't':
                        return new Token(TokenType.BOOLEAN, String.valueOf(isBoolean()), lineNum, position);
                    case 'n':
                        return new Token(TokenType.NULL, getNull(), lineNum, position);
                    case '{':
                        nextChar();
                        return new Token(TokenType.BEGIN_OBJECT, '{', lineNum, position);
                    case '}':
                        nextChar();
                        return new Token(TokenType.END_OBJECT, '}', lineNum, position);
                    default:
                        if(ch >= '0' && ch <= '9') {
                            return new Token(TokenType.NUMBER, getNumber(), lineNum, position);
                        }
                        error("invalid character "+ ch);
                }

            }
            nextChar();

            if (ch == '\n') {
                lineNum++;
                position = 1;
            }
        }
        return new Token(TokenType.END_DOCUMENT, "EOF", lineNum, position);
    }

    private String getNumber() throws IOException {
        StringBuilder sb = new StringBuilder();
        StringBuilder fraction = new StringBuilder();
        StringBuilder exr = new StringBuilder();
        boolean isFraction = false;
        boolean isExr = false;
        boolean isMinus = false;
        boolean isNumberEnd = false;
        char currentChar = '?';
        char next = '?';
        if (pos < length) {
            currentChar = buffer[pos];
        }
        if (currentChar == '-') {
            isMinus = true;
            next = nextChar();
        }

        while (true) {
            if (!isNumberEnd) {
                next = nextChar();

                if (next >= '0' && next <= '9') {
                    if (isExr) {
                        exr.append(next);
                    } else if (isFraction) {
                        fraction.append(next);
                    } else
                        sb.append(next);
                } else if (next == '.') {
                    if (isExr) {
                        error("extra point");
                    }

                    if (isFraction) {
                        error("extra point");
                    }
                    isFraction = true;
                } else if (next == 'e' || next == 'E') {
                    if (isExr) {
                        error("extra character e");
                    }
                    isExr = true;
                } else {
                    if (sb.length() == 0) {
                        error("invalid number");
                    }
                    unread();
                    isNumberEnd = true;
                }
            } else {
                if (sb.length() == 0) {
                    error("invalid number");
                }

                if (!isMinus) {
                    if (!isExr) {
                        if (!isFraction) {
                            return sb.toString();
                        } else {
                            return sb.toString() + "." + fraction.toString();
                        }
                    } else {
                        if (!isFraction) {
                            return sb.toString() + "e" + exr.toString();
                        } else {
                            return sb.toString() + "." + fraction.toString() + "e" + exr.toString();
                        }
                    }

                } else {
                    if (!isFraction) {
                        return "-"+sb.toString();
                    } else {
                        return "-"+sb.toString() + "." + fraction.toString();
                    }
                }


            }
        }
    }

    private boolean isSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }

    public boolean isBoolean() throws IOException {
        char ch = nextChar();
        String res = "";
        if (ch == 't') {
            res = "true";
        } else {
            res = "false";
        }
        for (int i = 1;i < res.length();i++) {
            ch = nextChar();
            if (ch != res.charAt(i)) {
                error("expected "+res);
            }
        }
        return Boolean.valueOf(res);
    }

    public String getNull() throws IOException {
        String res = "null";
        for (int i = 0;i < res.length();i++) {
            char ch = nextChar();
            if (ch != res.charAt(i)) {
                error("expected null");
            }
        }
        return "null";
    }

    public String getString() throws IOException {
        StringBuilder sb = new StringBuilder();
        char ch = nextChar();

        if (ch != '\"') {
            throw new ParseException("line:"+ lineNum + " position: "+position+ " expected \"");
        } else {
            while (true) {
                if (!isEnd()) {
                    ch = nextChar();
                    if (ch == '\\') {
                        char ech = nextChar();
                        switch (ech) {
                            case '\"':
                                sb.append('\"');
                                break;
                            case '\\':
                                sb.append('\\');
                                break;
                            case '/':
                                sb.append('/');
                                break;
                            case 'b':
                                sb.append('\b');
                                break;
                            case 'f':
                                sb.append('\f');
                                break;
                            case 'n':
                                sb.append('\n');
                                break;
                            case 'r':
                                sb.append('\r');
                                break;
                            case 't':
                                sb.append('\t');
                                break;
                            default:
                                error("invalid char "+ch);
                        }
                    } else if (ch == '\"'){
                        break;
                    } else {
                        sb.append(ch);
                    }
                }

            }
            return sb.toString();
        }
    }
//    public Number getNumber() {
//
//    }
    private char nextChar() throws IOException {
        char c = buffer[pos];
        pos++;
        position++;
        return c;
    }

    private boolean isEnd() throws IOException {
        if (pos < length) {
            return false;
        }
        int temp = reader.read(buffer);
        if (temp != - 1) {
            length = temp;
            pos = 0;
        }
        return pos >= length;
    }

    private void unread() {
        pos--;
        position--;
    }

    private void error(String message) {
        throw new ParseException("line:"+ lineNum + " position: "+position+" "+message);
    }
}
