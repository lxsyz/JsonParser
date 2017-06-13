package model;

/**
 * Created by Administrator on 2017/6/10.
 */
public class Token {
    private TokenType type;
    private int lineNum;
    private int position;

    public int getPosition() {
        return position;
    }

    public int getLineNum() {
        return lineNum;
    }

    public Token(TokenType type, String value, int lineNum, int position) {
        this.type = type;
        this.value = value;
        this.lineNum = lineNum;
        this.position = position;
    }
    public Token(TokenType type, char c, int lineNum, int position) {
        this.type = type;
        this.value = c+"";
        this.lineNum = lineNum;
        this.position = position;
    }
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;


}
