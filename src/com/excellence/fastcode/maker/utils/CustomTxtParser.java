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
 *     time   : 2022/6/15
 *     desc   :
 * </pre> 
 */
public class CustomTxtParser {

    /**
     * code:3434
     * http://test2:1/c/
     * http://test2:1/c/
     * http://test2:1/c/
     *
     * ·
     * ·
     * ·
     */

    private static final String EXT_INF = "code";

    private static final String EXT_QUOTES = "\"";
    private static final String EXT_NEW_LINE = "\n";

    private Map<String, List<FastCode.Server>> mFastCodeMap = new LinkedHashMap<>();

    public static CustomTxtParser newInstance() {
        return new CustomTxtParser();
    }

    private CustomTxtParser() {
    }

    public static String parse(List<FastCode> fastCodeList) {
        StringBuilder content = new StringBuilder();
        for (FastCode item : fastCodeList) {

            StringBuilder sb = new StringBuilder();
            String code = item.getCode();
            sb.append(String.format("code:%s\n", code));

            for (FastCode.Server server : item.getServers()) {
                String url = isEmpty(server.getServer_url()) ? "" : server.getServer_url();
                sb.append(String.format("%s\n", url));
            }

            content.append(sb.toString()).append("\n");
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
                sb.append(line).append('\n');
                while ((line = br.readLine()) != null
                        && !line.startsWith(EXT_INF)) {
                    sb.append(line).append('\n');
                }

                Pair<String, List<FastCode.Server>> item = parseInfo(sb.toString());
                String code = item.getKey();
                if (isEmpty(code)) {
                    code = mFastCodeMap.keySet().toArray(new String[0])[mFastCodeMap.keySet().size() - 1];
                }

                if (mFastCodeMap.get(code) == null) {
                    mFastCodeMap.put(code, new ArrayList<FastCode.Server>());
                }
                List<FastCode.Server> fastCodeList = mFastCodeMap.get(code);
                fastCodeList.addAll(item.getValue());
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

    private Pair<String, List<FastCode.Server>> parseInfo(String toString) {
        String[] lines = toString.split("\n");

        String codeId = lines[0].substring(EXT_INF.length() + 1).trim();
        List<FastCode.Server> itemList = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (isEmpty(line)) {
                continue;
            }
            FastCode.Server item = new FastCode.Server();
            item.setServer_url(line);
            itemList.add(item);
        }
        return new Pair<>(codeId, itemList);
    }

    public static void saveFile(File savedFile, List<FastCode> fastCodeList) throws Exception {
        String content = parse(fastCodeList);

        FileOutputStream os = new FileOutputStream(savedFile);
        os.write(content.getBytes());
        os.close();
    }
}
