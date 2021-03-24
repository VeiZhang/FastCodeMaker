package com.excellence.fastcode.maker.utils;

import com.excellence.fastcode.maker.entity.FastCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.excellence.fastcode.maker.utils.EmptyUtils.isEmpty;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2021/3/23
 *     desc   :
 * </pre> 
 */
public class JsonParser {

    /**
     * [
     *   {
     *     "code":"1",
     *     "servers":[
     *       {
     *         "server_name":"Hello",
     *         "server_url":"http://1.com",
     *         "server_mac":"11:11:11:11:11:11",
     *         "user_name":"Test",
     *         "user_password":"123"
     *       }
     *     ]
     *   }
     * ]
     */

    private List<FastCode> mFastCodeList = new ArrayList<>();

    public static JsonParser newInstance() {
        return new JsonParser();
    }

    private JsonParser() {
    }

    public String parse(File file) throws Exception {
        Gson gson = new Gson();
        mFastCodeList = gson.fromJson(new InputStreamReader(new FileInputStream(file)),
                new TypeToken<List<FastCode>>() {
                }.getType());
        return formatJson(gson.toJson(mFastCodeList));
    }

    public String parse(List<FastCode> fastCodeList) {
        mFastCodeList = fastCodeList;
        return parse();
    }

    public List<FastCode> getFastCodeList() {
        return mFastCodeList;
    }

    public boolean addItem(String code, FastCode.Server serverItem) {
        boolean success = false;
        if (mFastCodeList == null
                || isEmpty(code) || isEmpty(serverItem)) {
            return success;
        }

        boolean codeExist = false;
        for (FastCode fastCode : mFastCodeList) {
            if (fastCode.getCode().equals(code)) {
                codeExist = true;

                boolean isExist = false;
                if (fastCode.getServers() == null) {
                    fastCode.setServers(new ArrayList<FastCode.Server>());
                }

                for (FastCode.Server server : fastCode.getServers()) {
                    if (server.equals(serverItem)) {
                        isExist = true;
                    }
                }
                if (!isExist) {
                    fastCode.getServers().add(serverItem);
                    success = true;
                }
            }
        }
        if (!codeExist) {
            FastCode fastCode = new FastCode();
            fastCode.setCode(code);
            List<FastCode.Server> serverList = new ArrayList<>();
            serverList.add(serverItem);
            fastCode.setServers(serverList);

            mFastCodeList.add(fastCode);
            success = true;
        }

        Collections.sort(mFastCodeList, new Comparator<FastCode>() {
            @Override
            public int compare(FastCode o1, FastCode o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });
        return success;
    }

    public String parse() {
        Gson gson = new Gson();
        return formatJson(gson.toJson(mFastCodeList));
    }

    /**
     * 格式化
     *
     * @param jsonStr
     * @return
     * @author lizhgb
     * @Date 2015-10-14 下午1:17:35
     * @Modified 2017-04-28 下午8:55:35
     */
    private static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr))
            return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\') {
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent++;
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '}':
                case ']':
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\' && !isInQuotationMarks) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加space
     *
     * @param sb
     * @param indent
     * @author lizhgb
     * @Date 2015-10-14 上午10:38:04
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
    }

    public static void saveFile(File savedFile, String content) {
        try {
            FileOutputStream os = new FileOutputStream(savedFile);
            os.write(content.getBytes());
            os.close();
        } catch (Exception e) {
            AlertKit.showErrorAlert("Save file error", e);
        }
    }
}
