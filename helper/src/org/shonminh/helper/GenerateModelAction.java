package org.shonminh.helper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class GenerateModelAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();
        String sqlStr = "";
        if (selectionModel.hasSelection()) {
            sqlStr = selectionModel.getSelectedText();
        } else {
            sqlStr = document.getText();
        }

        CharSequence charsSequence = document.getCharsSequence();
        Messages.showInfoMessage(charsSequence.toString(), "Demo Editor");
    }
}
