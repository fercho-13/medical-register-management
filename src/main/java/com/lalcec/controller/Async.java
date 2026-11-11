package com.lalcec.controller;

import javax.swing.*;
import java.awt.Component;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/** Ejecuta trabajo de BD fuera del hilo de Swing (SwingWorker) y vuelve al EDT con el resultado o el error. */
public final class Async {
    private Async() {}

    public static <T> void run(Component parent, Callable<T> work, Consumer<T> onSuccess) {
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return work.call(); }
            @Override protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(parent, "Error: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
