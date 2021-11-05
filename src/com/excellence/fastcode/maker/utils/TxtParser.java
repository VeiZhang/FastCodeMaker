package com.excellence.fastcode.maker.utils;

import com.excellence.fastcode.maker.entity.FastCode;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

import static com.excellence.fastcode.maker.utils.EmptyUtils.isEmpty;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2021/11/4
 *     desc   :
 * </pre> 
 */
public class TxtParser {

    /**
     * #FASTCODE
     * code="0002"
     * server-name="22"
     * server-url="http://test2:1/c/"
     * server-mac="11:11:79:aa:99:aa"
     * user-name="test2"
     * user-password="123"
     *
     * #FASTCODE
     * ·
     * ·
     * ·
     */

    private static final String EXT_INF = "#FASTCODE";

    private static final String EXT_QUOTES = "\"";
    private static final String EXT_NEW_LINE = "\n";
    private static final String EXT_EQUAL = "=";

    private static final String ATTR_ID = "code";
    private static final String ATTR_SERVER_NAME = "server-name";
    private static final String ATTR_SERVER_URL = "server-url";
    private static final String ATTR_SERVER_MAC = "server-mac";
    private static final String ATTR_USER_NAME = "user-name";
    private static final String ATTR_USER_PASSWORD = "user-password";

    private Map<String, List<FastCode.Server>> mFastCodeMap = new LinkedHashMap<>();

    public static TxtParser newInstance() {
        return new TxtParser();
    }

    private TxtParser() {
    }

    public static String parse(List<FastCode> fastCodeList) {
        StringBuilder content = new StringBuilder();
        for (FastCode item : fastCodeList) {

            for (FastCode.Server server : item.getServers()) {
                String serverName = isEmpty(server.getServer_name()) ? "" : server.getServer_name();
                String mac = isEmpty(server.getServer_mac()) ? "" : server.getServer_mac();
                String userName = isEmpty(server.getUser_name()) ? "" : server.getUser_name();
                String password = isEmpty(server.getUser_password()) ? "" : server.getUser_password();
                String url = isEmpty(server.getServer_url()) ? "" : server.getServer_url();

                String itemLine = String.format("#FASTCODE\n" +
                                "code=\"%s\"\n" +
                                "server-name=\"%s\"\n" +
                                "server-url=\"%s\"\n" +
                                "server-mac=\"%s\"\n" +
                                "user-name=\"%s\"\n" +
                                "user-password=\"%s\"\n" +
                                "\n",
                        item.getCode(), serverName, url, mac, userName, password);
                content.append(itemLine);
            }
        }
        return content.toString();
    }

    public List<FastCode> parse(File file) throws Exception {
        mFastCodeMap = new LinkedHashMap<>();

        InputStream is = new FileInputStream(file);

        InputStreamReader ir = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(ir);

        String line = br.readLine();
        while (line != null) {
            if (line.startsWith(EXT_INF)) {
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null
                        && !line.startsWith(EXT_INF)) {
                    sb.append(line).append('\n');
                }

                Pair<String, FastCode.Server> item = parseInfo(sb.toString());
                String code = item.getKey();
                if (isEmpty(code)) {
                    code = mFastCodeMap.keySet().toArray(new String[0])[mFastCodeMap.keySet().size() - 1];
                }

                if (mFastCodeMap.get(code) == null) {
                    mFastCodeMap.put(code, new ArrayList<FastCode.Server>());
                }
                List<FastCode.Server> fastCodeList = mFastCodeMap.get(code);
                fastCodeList.add(item.getValue());
            } else {
                line = br.readLine();
            }
        }
        br.close();

        List<FastCode> fastCodeList = new ArrayList<>();
        for (String code : mFastCodeMap.keySet()) {
            FastCode fastCode = new FastCode();
            fastCode.setCode(code);
            fastCode.setServers(mFastCodeMap.get(code));
            fastCodeList.add(fastCode);
        }
        return fastCodeList;
    }

    private Pair<String, FastCode.Server> parseInfo(String toString) {
        FastCode.Server item = new FastCode.Server();

        String codeId = getAttr(toString, ATTR_ID);
        String url = getAttr(toString, ATTR_SERVER_URL);
        String serverName = getAttr(toString, ATTR_SERVER_NAME);
        String mac = getAttr(toString, ATTR_SERVER_MAC);
        String userName = getAttr(toString, ATTR_USER_NAME);
        String password = getAttr(toString, ATTR_USER_PASSWORD);

        item.setServer_url(url);
        item.setServer_name(isEmpty(serverName) ? null : serverName);
        item.setServer_mac(isEmpty(mac) ? null : mac);
        item.setUser_name(isEmpty(userName) ? null : userName);
        item.setUser_password(isEmpty(password) ? null : password);
        return new Pair<>(codeId, item);
    }

    private static String getAttr(String info, String key) {
        int index = info.indexOf(key) + key.length() + 1;
        int lastIndex = info.indexOf(EXT_NEW_LINE, index);
        try {
            String value = info.substring(index + 1, lastIndex);
            return value.replaceAll(EXT_QUOTES, "").trim();
        } catch (Exception ignore) {

        }
        return "";
    }

    public static void saveFile(File savedFile, List<FastCode> fastCodeList) throws Exception {
        String content = parse(fastCodeList);

        FileOutputStream os = new FileOutputStream(savedFile);
        os.write(content.getBytes());
        os.close();
    }
}
