package com.nauczycielka.service.ai;

import com.nauczycielka.dto.ai.OllamaRequest;
import com.nauczycielka.dto.ai.OllamaResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface OllamaClient {

    @PostMapping("/api/generate")
    OllamaResponse generate(@RequestBody OllamaRequest request);
}
