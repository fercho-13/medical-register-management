package com.lalcec.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

final class Fechas {
    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Fechas() {}

    static String fmt(LocalDate d) { return d == null ? "" : d.format(FMT); }

    /** Vacío -> null. Formato inválido -> IllegalArgumentException con mensaje legible. */
    static LocalDate parse(String s, String campo) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(s.trim(), FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(campo + ": use el formato dd/mm/aaaa");
        }
    }

    static String nullIfEmpty(String s) { return s == null || s.trim().isEmpty() ? null : s.trim(); }
}
