import exception.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Administrator on 2017/6/9.
 */
public class Main {

    private static String srcFilename = "";
    public static void main(String[] args) throws Exception {

        int len = args.length;
        if (len > 2) {
            throw new ParseException("too many parameter");
        }
        if (len < 2) {
            throw new ParseException("lack of parameter, please use like json -pretty filename");
        }
        if (!args[0].equals("-pretty")) {
            throw new ParseException("wrong parameter " + args[0] + " expect -pretty");
        }
        srcFilename = args[1];
        //解析
        GrammarParser grammarParser = new GrammarParser(srcFilename);
        HashMap<String, Object> map = (HashMap<String, Object>)grammarParser.parse();
        System.out.println("Valid");
        //格式化
        formatToFile(srcFilename);
        //查询路径
        System.out.println("查询路径用法示例: /RECORDS[1]/countryname");
        String path = new Scanner(System.in).next();
        customizePath(map, path);
    }

    /**
     * 自定义查询路径
     * @param map
     * @param path
     */
    private static void customizePath(HashMap<String, Object> map,String path) {
        String[] pathArr = path.split("/");
        int length = pathArr.length;
        Object res = map;
        try {
            for (int i = 1; i < length; i++) {
                String str = pathArr[i];
                if (str.contains("[")) {
                    String temp = str.substring(0, str.indexOf('['));
                    ArrayList<Object> arr = new ArrayList<>();
                    arr = ((HashMap<String, ArrayList<Object>>)res).get(temp);
                    int index = Integer.parseInt(str.substring(str.indexOf('[') + 1, str.indexOf(']')));
                    res = arr.get(index);
                } else {
                    res = ((HashMap<String, Object>)res).get(str);
                }
            }
        } catch (Exception e) {
            System.out.println("null");
        }
        if (res instanceof Integer) {
            System.out.println("整数："+ res);
        } else if (res instanceof String) {
            System.out.println("字符串："+ res);
        } else if (res instanceof Double) {
            System.out.println("浮点数："+ res);
        } else if (res instanceof ArrayList) {
            System.out.println("数组："+ res);
        } else if (res instanceof HashMap) {
            System.out.println("JSON对象："+ res);
        } else if (res instanceof Boolean) {
            System.out.println("布尔：" +res);
        }
//        System.out.println(res.getClass());

    }
    /**
     * 格式化输出至文件
     * @param srcFilename
     * @throws IOException
     */
    private static void formatToFile(String srcFilename) throws IOException {
        FileReader fr = new FileReader(srcFilename);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String line = null;

        String desFilename = srcFilename.substring(0, srcFilename.indexOf('.')) + ".pretty.json";

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
//        System.out.println(format(sb.toString()));
        String des = format(sb.toString());
        File f = new File(desFilename);
        if (!f.exists()) {
            f.createNewFile();
        }

        FileOutputStream out = new FileOutputStream(f);

        out.write(des.getBytes());
    }

    /**
     * 格式化
     * @param str
     * @return
     */
    public static String format(String str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        //大括号的次数
        int braceNumber = 0;
        for (int i = 0;i < length;i++) {
            char c = str.charAt(i);
            //大括号的情况
            if (c == '{') {
                if (i != 0 && str.charAt(i - 1) == ':') {
                    sb.append("\r\n");
                    sb.append(indent(braceNumber));
                }
                sb.append(c);
                sb.append("\r\n");
                braceNumber++;
                sb.append(indent(braceNumber));
                continue;
            }

            if (c == '[') {
                if (i != 0 && str.charAt(i - 1) == ':') {
                    sb.append(' ');
                }
                sb.append(c);
                sb.append("\r\n");
                sb.append(indent(++braceNumber));
                continue;
            }

            if (c == '}' || c == ']') {
                sb.append("\r\n");
                sb.append(indent(--braceNumber));
                sb.append(c);
                continue;
            }

            if (c == ',') {
                sb.append(c);
                sb.append("\r\n");
                sb.append(indent(braceNumber));
                continue;
            }
//            System.out.println(c);
            if (c != ' ' && c != '\t')
                sb.append(c);
        }
        return sb.toString();
    }

    /**
     *
     * @param count 缩进几次
     * @return
     */
    private static String indent(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < count;i++) {
            sb.append("    ");
        }
        return sb.toString();
    }
}
