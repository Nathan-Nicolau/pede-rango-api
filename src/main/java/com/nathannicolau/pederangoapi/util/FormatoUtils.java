package com.nathannicolau.pederangoapi.util;

public class FormatoUtils {

    public static String formatarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;
        String limpo = cpf.replaceAll("\\D", "");
        if (limpo.length() == 11) {
            return String.format("%s.%s.%s-%s",
                limpo.substring(0, 3),
                limpo.substring(3, 6),
                limpo.substring(6, 9),
                limpo.substring(9, 11)
            );
        }
        return cpf.trim();
    }

    public static String formatarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) return null;
        String limpo = telefone.replaceAll("\\D", "");
        if (limpo.length() == 11) {
            return String.format("(%s) %s-%s",
                limpo.substring(0, 2),
                limpo.substring(2, 7),
                limpo.substring(7, 11)
            );
        } else if (limpo.length() == 10) {
            return String.format("(%s) %s-%s",
                limpo.substring(0, 2),
                limpo.substring(2, 6),
                limpo.substring(6, 10)
            );
        }
        return telefone.trim();
    }

    public static String formatarEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return email.trim().toLowerCase();
    }
}
