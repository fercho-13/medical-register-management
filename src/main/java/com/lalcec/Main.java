package com.lalcec;

import com.lalcec.view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // se mantiene el look and feel por defecto
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
