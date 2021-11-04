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
 *     time   : 2021/3/23
 *     desc   :
 * </pre> 
 */
public class M3uParser {

    /**
     * #FASTCODE:-1 code-ID="0002" server-name="22" server-mac="11:11:79:aa:99:aa" user-name="test2" user-password="123"
     * http://test2:1/c/
     */

    private static final String EXT_INF = "#FASTCODE:";
    private static final String EXT_SPACE = " ";
    private static final String EXT_QUOTES = "\"";

    private static final String EXT_SLASH_QUOTES = "\\\"";
    private static final String EXT_NEW_LINE = "\n";
    private static final String EXT_EQUAL = "=";

    private static final String ATTR_ID = "code-ID";
    private static final String ATTR_SERVER_NAME = "server-name";
    private static final String ATTR_SERVER_MAC = "server-mac";
    private static final String ATTR_USER_NAME = "user-name";
    private static final String ATTR_USER_PASSWORD = "user-password";

    private Map<String, List<FastCode.Server>> mFastCodeMap = new LinkedHashMap<>();

    public static M3uParser newInstance() {
        return new M3uParser();
    }

    private M3uParser() {
    }

    /**
     *
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<FastCode> parse(File file) throws Exception {
        mFastCodeMap = new LinkedHashMap<>();

        InputStream is = new FileInputStream(file);

        InputStreamReader ir = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(ir);

        String line = br.readLine();
        while (line != null) {
            if (line.startsWith(EXT_INF)) {
                StringBuilder sb = new StringBuilder(line);
                while ((line = br.readLine()) != null
                        && !line.startsWith(EXT_INF)) {
                    sb.append('\n').append(line);

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
                    break;
                }
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

    private static Pair<String, FastCode.Server> parseInfo(String line) {
        FastCode.Server item = new FastCode.Server();
        /**
         * 取URL，从第一个\n到结束
         */
        int index = line.indexOf(EXT_NEW_LINE);
        String url = line.substring(index + EXT_NEW_LINE.length());
        item.setServer_url(url.trim());
        line = line.substring(0, index);

        /**
         * 取,，从最后一个"开始，如果没有"，则从开始位置查，需要排除节目名中的\"
         */
        int extQuotesIndex = line.length();
        do {
            String tempLine = line.substring(0, extQuotesIndex);
            extQuotesIndex = tempLine.lastIndexOf(EXT_QUOTES);
            /**
             * 判断是否遇到了\"，而不是"
             */
            int extSlashQuotesIndex = tempLine.lastIndexOf(EXT_SLASH_QUOTES);
            if (extSlashQuotesIndex == (extQuotesIndex - 1)) {
                extQuotesIndex--;
            } else {
                break;
            }

            if (extQuotesIndex <= 0) {
                break;
            }
        } while (true);

        /**
         * ↓
         * ↓
         * ↓
         * -1 tvg-id="RTL4.nl" tvg-name="||NL|| RTL 4 HD" tvg-logo="http://tv.trexiptv.com:8000/picons/logos/rtl4.png" group-title="NEDERLAND HD"
         * -1
         */

        index = line.indexOf(EXT_SPACE);
        int length = line.length();
        if (index == -1) {
            index = length;
        }

        /**
         * 有空格则+1，无空格表示没有其他信息
         */
        index++;
        String codeId = "";
        if (index < length) {
            line = line.substring(index).trim();
            codeId = getAttr(line, ATTR_ID);

            String serverName = getAttr(line, ATTR_SERVER_NAME);
            String mac = getAttr(line, ATTR_SERVER_MAC);
            String userName = getAttr(line, ATTR_USER_NAME);
            String password = getAttr(line, ATTR_USER_PASSWORD);

            item.setServer_name(isEmpty(serverName) ? null : serverName);
            item.setServer_mac(isEmpty(mac) ? null : mac);
            item.setUser_name(isEmpty(userName) ? null : userName);
            item.setUser_password(isEmpty(password) ? null : password);
        }

        return new Pair<>(codeId, item);
    }

    private static String getAttr(String info, String key) {
        String value = null;
        String lowerCaseInfo = info.toLowerCase();
        String keyLowerCase = key.toLowerCase();
        if (lowerCaseInfo.contains(keyLowerCase)) {
            /**
             * tvg-id="RTL4.nl"
             * 先找tvg-id=为起始位置
             * 再找第二个引号为结束位置
             * 最后清除引号和空格
             *
             * 这里注意转义字符
             */
            int startIndex = lowerCaseInfo.indexOf(keyLowerCase) + key.length() + EXT_EQUAL.length() + 1;
            int endIndex = 0;
            int searchQuotesIndex = startIndex;
            do {
                endIndex = info.indexOf(EXT_QUOTES, searchQuotesIndex);
                /**
                 * 判断是否遇到了\"，而不是"
                 */
                int extSlashQuotesIndex = info.indexOf(EXT_SLASH_QUOTES, searchQuotesIndex);
                if (extSlashQuotesIndex == (endIndex - 1)) {
                    searchQuotesIndex = extSlashQuotesIndex + EXT_SLASH_QUOTES.length();
                } else {
                    break;
                }
                if (searchQuotesIndex >= info.length()) {
                    break;
                }
            } while (true);
            value = info.substring(startIndex, endIndex).trim();
        }
        return value;
    }

    public static void saveFile(File savedFile, List<FastCode> fastCodeList) {
        try {
            StringBuilder content = new StringBuilder();
            for (FastCode item : fastCodeList) {

                for (FastCode.Server server : item.getServers()) {
                    String serverName = isEmpty(server.getServer_name()) ? "" : server.getServer_name();
                    String mac = isEmpty(server.getServer_mac()) ? "" : server.getServer_mac();
                    String userName = isEmpty(server.getUser_name()) ? "" : server.getUser_name();
                    String password = isEmpty(server.getUser_password()) ? "" : server.getUser_password();
                    String url = isEmpty(server.getServer_url()) ? "" : server.getServer_url();

                    String itemLine = String.format("#FASTCODE:-1 code-ID=\"%s\" server-name=\"%s\" server-mac=\"%s\" user-name=\"%s\" user-password=\"%s\"\n%s",
                            item.getCode(), serverName, mac, userName, password, url);
                    if (fastCodeList.indexOf(item) != fastCodeList.size() - 1) {
                        itemLine += "\n";
                    }
                    content.append(itemLine);
                }
            }

            FileOutputStream os = new FileOutputStream(savedFile);
            os.write(content.toString().getBytes());
            os.close();
        } catch (Exception e) {
            AlertKit.showErrorAlert("Save file error", e);
        }
    }
}
