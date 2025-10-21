package ru.gigafood.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.core.io.InputStreamResource;

@Service
public class AiWebClientService {

    private final WebClient webClient = WebClient.create("http://fastapi:8000");

    public String classifyFood(MultipartFile file) throws Exception {
        InputStreamResource resource = new InputStreamResource(file.getInputStream()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }

            @Override
            public long contentLength() {
                return file.getSize();
            }
        };

        return webClient.post()
                .uri("/classify_food")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", resource))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String generateText(String prompt, int maxTokens) {
        return webClient.post()
                .uri("/generate_text")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("prompt", prompt)
                                   .with("max_tokens", String.valueOf(maxTokens)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
