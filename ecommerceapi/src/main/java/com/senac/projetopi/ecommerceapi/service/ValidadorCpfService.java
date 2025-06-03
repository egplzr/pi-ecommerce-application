package com.senac.projetopi.ecommerceapi.service;

import org.springframework.stereotype.Service;

@Service
public class ValidadorCpfService {

    public boolean validar(String cpf) {
        if (cpf == null) {
            return false;
        }

        cpf = cpf.replaceAll("[^0-9]", ""); // Remove tudo que não for dígito

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (ex: 111.111.111-11) - CPFs assim são inválidos
        boolean todosDigitosIguais = true;
        for (int i = 1; i < 11; i++) {
            if (cpf.charAt(i) != cpf.charAt(0)) {
                todosDigitosIguais = false;
                break;
            }
        }
        if (todosDigitosIguais) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int soma = 0;
        int peso = 10;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * peso;
            peso--;
        }
        int primeiroDigitoVerificador = 11 - (soma % 11);
        if (primeiroDigitoVerificador == 10 || primeiroDigitoVerificador == 11) {
            primeiroDigitoVerificador = 0;
        }

        // Compara com o primeiro dígito do CPF
        if (primeiroDigitoVerificador != (cpf.charAt(9) - '0')) {
            return false;
        }

        // Calcula o segundo dígito verificador
        soma = 0;
        peso = 11;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * peso;
            peso--;
        }
        int segundoDigitoVerificador = 11 - (soma % 11);
        if (segundoDigitoVerificador == 10 || segundoDigitoVerificador == 11) {
            segundoDigitoVerificador = 0;
        }

        // Compara com o segundo dígito do CPF
        return segundoDigitoVerificador == (cpf.charAt(10) - '0');
    }
}
