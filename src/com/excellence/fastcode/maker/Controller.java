package com.excellence.fastcode.maker;


import com.excellence.fastcode.maker.entity.FastCode;
import com.excellence.fastcode.maker.events.FileDragEventHandler;
import com.excellence.fastcode.maker.utils.AlertKit;
import com.excellence.fastcode.maker.utils.JsonParser;
import com.excellence.fastcode.maker.utils.M3uParser;
import com.excellence.fastcode.maker.utils.TxtParser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import static com.excellence.fastcode.maker.utils.EmptyUtils.isEmpty;
import static com.excellence.fastcode.maker.utils.EmptyUtils.isNotEmpty;

public class Controller implements Initializable {

    private static final String SUFFIX_M3U = ".m3u";
    private static final String SUFFIX_JSON = ".json";

    @FXML
    private TextField fastcodeFilePathTv;
    @FXML
    private Button openFastcodeFileBtn;

    @FXML
    public AnchorPane root;
    @FXML
    private TextField codeTv;
    @FXML
    private TextField serverNameTv;
    @FXML
    public TextField serverUrlTv;
    @FXML
    public TextField macTv;
    @FXML
    public TextField userNameTv;
    @FXML
    public TextField passwordTv;
    @FXML
    public Button itemAddBtn;
    @FXML
    public TextArea contentTv;
    @FXML
    public Button exportFileBtn;
    @FXML
    public Button contentTypeBtn;

    private Stage mPrimaryStage;
    private String mFileChooserInitPath = null;
    private final JsonParser mJsonParser = JsonParser.newInstance();
    private final M3uParser mM3uParser = M3uParser.newInstance();
    private final TxtParser mTxtParser = TxtParser.newInstance();

    private int mContentType = 0;

    public void setStage(Stage primaryStage) {
        mPrimaryStage = primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * 拖拽文件到编辑框，显示路径
         */
        root.setOnDragOver(new FileDragEventHandler("*.json", "*.txt", "*.m3u"));
    }

    private void loadFastCodeFile() {
        String path = fastcodeFilePathTv.getText();
        try {
            String content = "";

            File pathFile = new File(path);
            if (path.endsWith(SUFFIX_M3U)) {
                content = mJsonParser.parse(mM3uParser.parse(pathFile));
            } else if (path.endsWith(SUFFIX_JSON)) {
                content = mJsonParser.parse(pathFile);
            } else {
                content = mJsonParser.parse(mTxtParser.parse(pathFile));
            }
            loadContent();
        } catch (Exception exception) {
            fastcodeFilePathTv.setText(null);
            contentTv.setText(null);
            AlertKit.showErrorAlert("Parse file error", exception);
        }
    }

    public void dragDropEvent(DragEvent event) {
        fastcodeFilePathTv.setText(getDragDropFilePath(event));
        loadFastCodeFile();
    }

    private String getDragDropFilePath(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            mFileChooserInitPath = dragboard.getFiles().get(0).getParent();
            return dragboard.getFiles().get(0).getPath();
        }
        return null;
    }

    public void selectFileEvent(ActionEvent event) {
        File file = getChooserFile("Json Txt or m3u file", "*.json", "*.txt", "*.m3u");
        if (file != null) {
            fastcodeFilePathTv.setText(file.getPath());
            loadFastCodeFile();
        }
    }

    private File getChooserFile(final String description, final String... extensions) {
        FileChooser fileChooser = newFileChooser();

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(description, extensions);
        fileChooser.getExtensionFilters().add(extensionFilter);

        File selectedFile = fileChooser.showOpenDialog(mPrimaryStage);
        if (selectedFile != null) {
            mFileChooserInitPath = selectedFile.getParent();
        }
        return selectedFile;
    }

    private FileChooser newFileChooser() {
        FileChooser fileChooser = new FileChooser();
        if (mFileChooserInitPath != null) {
            File initFile = new File(mFileChooserInitPath);
            if (initFile.exists()) {
                fileChooser.setInitialDirectory(initFile);
            }
        }
        return fileChooser;
    }

    public void saveFileEvent(ActionEvent actionEvent) {
        FileChooser fileChooser = newFileChooser();
        fileChooser.setInitialFileName("maker_fastcode");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Json file", "*.json"),
                new FileChooser.ExtensionFilter("Txt file", "*.txt"),
                new FileChooser.ExtensionFilter("m3u file", "*.m3u"));

        File savedFile = fileChooser.showSaveDialog(mPrimaryStage);
        String jsonContent = contentTv.getText();

        if (savedFile != null) {
            if (isEmpty(jsonContent)) {
                AlertKit.showErrorAlert("Content can't be empty");
            } else {
                mFileChooserInitPath = savedFile.getParent();
                saveFile(savedFile, jsonContent, fileChooser.getSelectedExtensionFilter().getExtensions());
            }
        }
    }

    private void saveFile(File savedFile, String jsonContent, List<String> extensions) {
        if (savedFile != null) {
            if (isNotEmpty(extensions)) {
                String suffix = extensions.get(0);
                try {
                    if (suffix.endsWith(SUFFIX_JSON)) {
                        JsonParser.saveFile(savedFile, jsonContent);
                    } else if (suffix.endsWith(SUFFIX_M3U)) {
                        M3uParser.saveFile(savedFile, mJsonParser.getFastCodeList());
                    } else {
                        TxtParser.saveFile(savedFile, mJsonParser.getFastCodeList());
                    }

                    Runtime.getRuntime().exec(String.format("explorer.exe /select,%s", savedFile));
                } catch (Exception e) {
                    AlertKit.showErrorAlert("Save file error", e);
                }
            }
        }
    }

    public void addItemEvent(ActionEvent actionEvent) {
        String code = codeTv.getText();
        String serverURL = serverUrlTv.getText();
        String serverName = serverNameTv.getText();
        String mac = macTv.getText();
        String userName = userNameTv.getText();
        String password = passwordTv.getText();

        if (isEmpty(code)) {
            AlertKit.showErrorAlert("Code can't be empty");
            return;
        }

        selectContentCode(code);
        if (isEmpty(serverURL)) {
            AlertKit.showErrorAlert("Server URL can't be empty");
            return;
        }

        FastCode.Server server = new FastCode.Server();
        server.setServer_url(isEmpty(serverURL) ? null : serverURL);
        server.setServer_name(isEmpty(serverName) ? null : serverName);
        server.setServer_mac(isEmpty(mac) ? null : mac);
        server.setUser_name(isEmpty(userName) ? null : userName);
        server.setUser_password(isEmpty(password) ? null : password);

        if (mJsonParser.addItem(code, server)) {
            loadContent();
        }

        selectContentCode(code);
    }

    private void selectContentCode(String code) {
        String content = contentTv.getText();
        String codeContent = String.format("\"code\":\"%s\"", code);
        if (content.contains("code=")) {
            codeContent = String.format("code=\"%s\"", code);
        }
        int index = content.indexOf(codeContent);
        if (index >= 0) {
            contentTv.selectRange(index, index + codeContent.length());
        }
    }

    public void typeChangeEvent(ActionEvent actionEvent) {
        mContentType++;
        loadContent();
    }

    private void loadContent() {
        String text = "Type:txt";
        String jsonContent = mJsonParser.parse();
        switch (mContentType % 3) {
            case 0:
            default:
                text = "Type:txt";
                contentTv.setText(TxtParser.parse(mJsonParser.getFastCodeList()));
                break;

            case 1:
                text = "Type:json";
                contentTv.setText(jsonContent);
                break;

            case 2:
                text = "Type:m3u";
                contentTv.setText(M3uParser.parse(mJsonParser.getFastCodeList()));
                break;
        }
        contentTypeBtn.setText(text);
    }
}
