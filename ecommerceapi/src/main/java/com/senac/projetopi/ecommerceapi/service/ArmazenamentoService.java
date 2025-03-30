package com.senac.projetopi.ecommerceapi.service;

import com.senac.projetopi.ecommerceapi.config.ArmazenamentoConfig;
import com.senac.projetopi.ecommerceapi.exception.ArmazenamentoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ArmazenamentoService {

    private final Path uploadPath;
    private final String uploadDir;

    @Autowired
    public ArmazenamentoService(ArmazenamentoConfig armazenamentoConfig) {
        this.uploadPath = armazenamentoConfig.getUploadPath();
        this.uploadDir = armazenamentoConfig.getUploadDir();

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new ArmazenamentoException("Não foi possível inicializar o diretório de armazenamento", e);
        }
    }

    public String armazenarArquivo(MultipartFile arquivo) {
        // Validar nome do arquivo
        String nomeOriginal = StringUtils.cleanPath(arquivo.getOriginalFilename());
        if (nomeOriginal.contains("..")) {
            throw new ArmazenamentoException("Nome de arquivo inválido: " + nomeOriginal);
        }

        // Gerar um nome único para evitar colisões
        String extensao = "";
        if (nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }
        String nomeArquivo = UUID.randomUUID().toString() + extensao;

        // Armazenar o arquivo
        try (InputStream inputStream = arquivo.getInputStream()) {
            Path destino = this.uploadPath.resolve(nomeArquivo);
            Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);

            // Retornar o caminho relativo para acesso via URL
            return "/uploads/" + nomeArquivo;
        } catch (IOException e) {
            throw new ArmazenamentoException("Falha ao armazenar o arquivo " + nomeArquivo, e);
        }
    }

    public Resource carregarArquivo(String nomeArquivo) {
        try {
            Path arquivo = this.uploadPath.resolve(nomeArquivo);
            Resource resource = new UrlResource(arquivo.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ArmazenamentoException("Não foi possível ler o arquivo: " + nomeArquivo);
            }
        } catch (MalformedURLException e) {
            throw new ArmazenamentoException("Não foi possível ler o arquivo: " + nomeArquivo, e);
        }
    }

    public void excluirArquivo(String caminho) {
        if (caminho == null || caminho.isEmpty()) {
            return;
        }

        // Extrair o nome do arquivo do caminho completo
        String nomeArquivo = caminho.substring(caminho.lastIndexOf("/") + 1);

        try {
            Path arquivo = this.uploadPath.resolve(nomeArquivo);
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            throw new ArmazenamentoException("Não foi possível excluir o arquivo: " + nomeArquivo, e);
        }
    }
}