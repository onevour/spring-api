package com.neta.teman.dawai.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neta.teman.dawai.api.applications.base.BaseService;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.ResourceUtils;
import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DocumentServiceImpl extends BaseService implements DocumentService {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DocumentRepository documentRepository;

    @Override
    public void initDocument() {
        try {
            int[] types = new int[]{Types.INTEGER};
            String values = ResourceUtils.asString(resourceLoader.getResource("classpath:master/document.json"));
            List<Document> documents = new ObjectMapper().readValue(values, new TypeReference<List<Document>>() {
            });
            for (Document document : documents) {
                log.info("role data {}", document.getName());
                Object[] params = new Object[]{document.getId()};
                String query = "SELECT COUNT(1) FROM APP_DOCUMENT WHERE ID = ?";
                int foundInDB = jdbcTemplate.queryForObject(query, params, Integer.class);
                if (0 == foundInDB) {
                    String sql = "insert into APP_DOCUMENT(id) values (?)";
                    int row = jdbcTemplate.update(sql, params, types);
                    log.info("create new role {}", row);
                }
                // update
                Document documentUpdate = documentRepository.findById(document.getId()).orElse(null);
                if (Objects.isNull(documentUpdate)) continue;
                BeanUtils.copyProperties(document, documentUpdate, "id");
                documentRepository.save(signature(documentUpdate));
            }
            log.info("\n{}", values);
        } catch (JsonProcessingException e) {
            log.error("error read json role", e);
        }
    }

    @Override
    public Document validType(Long type) {
        if (Objects.isNull(type)) return null;
        return documentRepository.findById(type).orElse(null);
    }

    @Override
    public ServiceResolver<List<Document>> loadAll() {
        return success(documentRepository.findAll());
    }
}
