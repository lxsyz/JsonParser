import exception.ParseException;
import model.Token;
import model.TokenType;
import reader.TokenReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Administrator on 2017/6/10.
 */
public class GrammarParser {
    private TokenReader reader;
    private Token currentToken;

    public GrammarParser(String file) throws IOException {
        reader = new TokenReader(file);
//        currentToken.setType(reader.nextToken());
        currentToken = reader.getToken();
    }

    private void next(TokenType type) throws Exception {
        if (currentToken.getType() == type) {
            currentToken = reader.getToken();
            return;
        }
        if (type == TokenType.SEP_COMMA) {
            error("line: " + currentToken.getLineNum() + " position " + currentToken.getPosition() + " unexpected token <,>");
        } else if (type == TokenType.SEP_COLON) {
            error("line: " + currentToken.getLineNum() + " position " + currentToken.getPosition() + " unexpected token <:>");
        } else if (type == TokenType.BEGIN_ARRAY) {
            error("line: " + currentToken.getLineNum() + " position " + currentToken.getPosition() + " unexpected token <[>");
        } else if (type == TokenType.END_ARRAY){
            error("line: " + currentToken.getLineNum() + " position " + currentToken.getPosition() + " unexpected token <]>");
        } else if (type == TokenType.BEGIN_OBJECT) {
            error("line: " + currentToken.getLineNum() + " position " + currentToken.getPosition() + " unexpected token <{>");
        } else if (type == TokenType.END_OBJECT) {
            error("line: " + currentToken.getLineNum() + " position " + currentToken.getPosition() + " unexpected token <}>");
        }
    }

    private void error(String message) throws Exception {
        throw new ParseException(message);
    }

    private ArrayList<Object> grammarList() throws Exception {
        ArrayList<Object> list = new ArrayList<>();
        list.add(parse());
        while (currentToken.getType() == TokenType.SEP_COMMA) {

            next(TokenType.SEP_COMMA);
            list.add(parse());

        }

        return list;
    }

    public Object parse() throws Exception {

        while (currentToken.getType() != TokenType.END_DOCUMENT) {
            if (currentToken.getType() == TokenType.NUMBER) {
//                System.out.println(currentToken.getValue());
                try {
                    int i = Integer.parseInt(currentToken.getValue());
                    next(TokenType.NUMBER);

                    return i;
                } catch (NumberFormatException e) {
                    double d = Double.parseDouble(currentToken.getValue());
                    next(TokenType.NUMBER);
                    return d;
                }
            }
            if (currentToken.getType() == TokenType.BEGIN_ARRAY) {

                next(TokenType.BEGIN_ARRAY);
                ArrayList<Object> list = grammarList();
                next(TokenType.END_ARRAY);
                return list;
            }

            if (currentToken.getType() == TokenType.BEGIN_OBJECT) {
                next(TokenType.BEGIN_OBJECT);
                HashMap<Object, Object> map = getJSONObject();

                next(TokenType.END_OBJECT);
                return map;
            }
            if (currentToken.getType() == TokenType.STRING) {
                String value = currentToken.getValue();
                next(TokenType.STRING);
                return value;
            }

            if (currentToken.getType() == TokenType.BOOLEAN) {
                String value = currentToken.getValue();
                next(TokenType.BOOLEAN);
                return Boolean.parseBoolean(value);
            }
            if (currentToken.getType() == TokenType.NULL) {
                String value = currentToken.getValue();
                next(TokenType.NULL);
                return value;
            }
        }
        return null;
    }

    private HashMap<Object, Object> getJSONObject() throws Exception {
        HashMap<Object, Object> map = new HashMap<>();
        Object k = parse();
//        next(TokenType.STRING);
        next(TokenType.SEP_COLON);
        Object v = parse();
        map.put(k, v);
        while (currentToken.getType() == TokenType.SEP_COMMA) {

            next(TokenType.SEP_COMMA);
            k = parse();
//            next(TokenType.STRING);
            next(TokenType.SEP_COLON);
            v = parse();
            map.put(k, v);

        }
        return map;
    }
}
