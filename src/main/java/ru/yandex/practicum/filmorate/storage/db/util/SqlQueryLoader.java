package ru.yandex.practicum.filmorate.storage.db.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SqlQueryLoader {
    
    private final Map<String, String> queryCache = new HashMap<>();

    public String getQuery(String fileName, String queryName) {
        String cacheKey = fileName + "." + queryName;
        
        if (queryCache.containsKey(cacheKey)) {
            return queryCache.get(cacheKey);
        }
        
        try {
            String content = loadFileContent("sql/" + fileName);
            String query = extractQuery(content, queryName);
            queryCache.put(cacheKey, query);
            return query;
        } catch (IOException e) {
            log.error("Ошибка загрузки SQL файла {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Не удалось загрузить SQL запрос: " + queryName, e);
        }
    }

    private String loadFileContent(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }

    private String extractQuery(String content, String queryName) {
        String commentPattern = "-- " + queryName;
        String[] lines = content.split("\n");
        
        StringBuilder queryBuilder = new StringBuilder();
        boolean inQuery = false;
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            if (trimmedLine.equals(commentPattern)) {
                inQuery = true;
                continue;
            }
            
            if (inQuery) {
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                    break;
                }
                
                queryBuilder.append(line).append("\n");
            }
        }
        
        String query = queryBuilder.toString().trim();
        if (query.isEmpty()) {
            throw new RuntimeException("SQL запрос '" + queryName + "' не найден");
        }
        
        return query;
    }
}
