package com.senac.projetopi.ecommerceapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários de ValidadorCpfService")
public class ValidadorCpfServiceTest {

    private ValidadorCpfService validadorCpfService;

    @BeforeEach
    void setUp() {
        // Instancia o serviço antes de cada teste
        validadorCpfService = new ValidadorCpfService();
    }

    @Test
    @DisplayName("Deve retornar true para um CPF válido")
    void deveRetornarTrueParaCpfValido() {
        // CPFs válidos para teste
        assertTrue(validadorCpfService.validar("123.456.789-09"), "CPF válido 1 com máscara");
        assertTrue(validadorCpfService.validar("12345678909"), "CPF válido 1 sem máscara");

        assertTrue(validadorCpfService.validar("448.585.338-16"), "CPF válido 2 com máscara");
        assertTrue(validadorCpfService.validar("44858533816"), "CPF válido 2 sem máscara");

        assertTrue(validadorCpfService.validar("124.756.960-87"), "CPF válido 3 com máscara");
        assertTrue(validadorCpfService.validar("12475696087"), "CPF válido 3 sem máscara");
    }

    @Test
    @DisplayName("Deve retornar false para CPF com tamanho incorreto")
    void deveRetornarFalseParaCpfComTamanhoIncorreto() {
        assertFalse(validadorCpfService.validar("1234567890"), "CPF com 10 dígitos");
        assertFalse(validadorCpfService.validar("123456789012"), "CPF com 12 dígitos");
        assertFalse(validadorCpfService.validar(""), "CPF vazio");
        assertFalse(validadorCpfService.validar(null), "CPF nulo (já tratado no serviço)");
        assertFalse(validadorCpfService.validar("123.456.789"), "CPF com máscara incompleta");
    }

    @Test
    @DisplayName("Deve retornar false para CPF com todos os dígitos iguais")
    void deveRetornarFalseParaCpfComTodosDigitosIguais() {
        assertFalse(validadorCpfService.validar("111.111.111-11"), "CPF com todos os dígitos 1");
        assertFalse(validadorCpfService.validar("22222222222"), "CPF com todos os dígitos 2");
        assertFalse(validadorCpfService.validar("00000000000"), "CPF com todos os dígitos 0");
        assertFalse(validadorCpfService.validar("99999999999"), "CPF com todos os dígitos 9");
    }

    @Test
    @DisplayName("Deve retornar false para CPF com primeiro dígito verificador inválido")
    void deveRetornarFalseParaCpfComPrimeiroDigitoVerificadorInvalido() {
        // CPF válido (123.456.789-09) com o primeiro DV alterado para 0 (resultaria em 9, mas forçamos 0)
        assertFalse(validadorCpfService.validar("12345678900"), "Primeiro dígito verificador incorreto (final 00)");
        // Exemplo: CPF 123.456.789-09. Se o primeiro dígito fosse 8:
        assertFalse(validadorCpfService.validar("12345678989"), "Primeiro dígito verificador incorreto (final 89)");
    }

    @Test
    @DisplayName("Deve retornar false para CPF com segundo dígito verificador inválido")
    void deveRetornarFalseParaCpfComSegundoDigitoVerificadorInvalido() {
        // CPF válido (123.456.789-09) com o segundo DV alterado para 1 (resultaria em 9, mas forçamos 1)
        assertFalse(validadorCpfService.validar("12345678901"), "Segundo dígito verificador incorreto (final 01)");
        // Exemplo: CPF 963.852.741-00. Se o segundo dígito fosse 1:
        assertFalse(validadorCpfService.validar("96385274101"), "Segundo dígito verificador incorreto (final 01)");
    }
}