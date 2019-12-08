package org.shonminh.helper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ui.Messages;
import org.shonminh.helper.sql.SqlParser;
import org.shonminh.helper.util.FileUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class GenerateModelAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();
        String sqlStr;
        if (selectionModel.hasSelection()) {
            sqlStr = selectionModel.getSelectedText();
        } else {
            sqlStr = document.getText();
        }
        Path filePath = Paths.get(Objects.requireNonNull(Objects.requireNonNull(FileDocumentManager.getInstance().getFile(editor.getDocument())).getCanonicalPath()));
        SqlParser sqlParser = new SqlParser();
        String result = sqlParser.Execute(sqlStr);
        String canonicalPath = filePath.getParent().toString() + "/" + sqlParser.getFileName();
        if ("".equals(result) || null == result) {
            Messages.showInfoMessage("create table not found", "Generate Model Result");
            return;
        }
        try {
            FileUtil.WriteFile(canonicalPath, result);
            Messages.showInfoMessage("success, file is " + canonicalPath, "Generate Model Result");
        } catch (IOException e) {
            Messages.showInfoMessage("failed, exception info is " + e.toString(), "Generate Model Result");
        }
    }
}
