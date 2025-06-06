package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FormatFileWriter {

    public void exportToXML(Map<String, String> localization, String filename) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        Map<String, Object> root = new HashMap<>();

        for (Map.Entry<String, String> entry : localization.entrySet()) {
            String[] keys = entry.getKey().split("\\.");
            addToNestedMap(root, keys, entry.getValue());
        }

        xmlMapper.writeValue(new File(filename), root);
    }

    public void exportToYAML(Map<String, String> localization, String filename) throws IOException {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(representer, options);
        Map<String, Object> root = new HashMap<>();

        for (Map.Entry<String, String> entry : localization.entrySet()) {
            String[] keys = entry.getKey().split("\\.");
            addToNestedMap(root, keys, entry.getValue());
        }

        yaml.dump(root, new FileWriter(filename));
    }

    public void exportToJSON(Map<String, String> localization, String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> root = new HashMap<>();

        for (Map.Entry<String, String> entry : localization.entrySet()) {
            String[] keys = entry.getKey().split("\\.");
            addToNestedMap(root, keys, entry.getValue());
        }

        objectMapper.writeValue(new File(filename), root);
    }

    private void addToNestedMap(Map<String, Object> map, String[] keys, String value) {
        Map<String, Object> currentMap = map;
        for (int i = 0; i < keys.length - 1; i++) {
            currentMap = (Map<String, Object>) currentMap.computeIfAbsent(keys[i], k -> new HashMap<>());
        }
        currentMap.put(keys[keys.length - 1], value);
    }
}