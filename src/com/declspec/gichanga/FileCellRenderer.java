package com.declspec.gichanga;

import java.awt.Component;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;

public class FileCellRenderer extends DefaultListCellRenderer  {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof File) {
            File file = (File) value;
            setText(file.getName());
            setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
        }
        return this;
    }
}


