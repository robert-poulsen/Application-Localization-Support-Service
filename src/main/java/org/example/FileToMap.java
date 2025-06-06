package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileToMap {
    private final MapToDatabase mapToDatabase;

    public FileToMap(MapToDatabase mapToDatabase) {
        this.mapToDatabase = mapToDatabase;
    }

    public void fileXML(File file, String language, int id, String projectName) {
        Map<String, String> map = new HashMap<>();
        try {
            // Створення фабрики для створення парсера XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Читання XML файлу та створення об'єкту Document
            InputStream inputStream = new FileInputStream(file);
            Document document = builder.parse(new InputSource(inputStream));

            // Отримання кореневого елементу документа
            Element root = document.getDocumentElement();

            String key = root.getNodeName();
            // Рекурсивно обробляємо елементи для перекладу текстів
            saveElementXML(map, key, root);

            mapToDatabase.saveUserTranslations(id, projectName, language, map);
        } catch (Exception e) {
            System.err.println("Помилка при обробці XML файлу: " + e.getMessage());
        }
    }

    private void saveElementXML(Map<String, String> map, String key, Element element) throws IOException {
        NodeList nodeList = element.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            //   key = node.getNodeName();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // Рекурсивно обробляємо дочірні елементи
                saveElementXML(map, key+"."+node.getNodeName(), (Element) node);

            } else if (node.getNodeType() == Node.TEXT_NODE) {
                // Обробляємо текстові вузли (елементи) для перекладу
                String text = node.getNodeValue().trim();
                if (!text.isEmpty()) {
                    map.put(key, text);
                    key = "";
                }
            }
        }
    }

    ///////////////////////////////////////////////////////

    public void fileYAMLandJSON(File file, String language, int id, String projectName){
        Map<String, String> map = new HashMap<>();
        try {
            // Створення об'єкту Yaml для обробки YAML файлу
            Yaml yaml = new Yaml();

            // Читання YAML файлу та перетворення його у об'єкт (Map)
            InputStream inputStream = new FileInputStream(file);
            Object yamlObject = yaml.load(inputStream);

            String key = "";
            // Рекурсивно обробляємо об'єкт для перекладу текстів
            saveElementYAMLandJSON(key, map, yamlObject);

            mapToDatabase.saveUserTranslations(id, projectName, language, map);
        } catch (Exception e) {
            System.err.println("Помилка при обробці YAML файлу: " + e.getMessage());
        }
    }

    private void saveElementYAMLandJSON(String key, Map<String, String> keyMap, Object yamlObject) throws IOException {
        // Рекурсивно обробляємо об'єкт для перекладу текстів
        if (yamlObject instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) yamlObject;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String newKey = key + entry.getKey() + ".";
                Object value = entry.getValue();
                if (value instanceof String) {
                    String text = (String) value;
                    newKey = newKey.substring(0, newKey.length() - 1);

                    keyMap.put(newKey, text);
                    map.put(entry.getKey(), text);
                } else {
                    saveElementYAMLandJSON(newKey, keyMap, value); // Рекурсивний виклик для вкладених об'єктів
                }
            }
        } else if (yamlObject instanceof List) {
            List<Object> list = (List<Object>) yamlObject;
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                String newKey = key + "[" + i + "]";
                if (item instanceof String) {
                    String text = (String) item;
                    keyMap.put(newKey, text);
                } else {
                    saveElementYAMLandJSON(newKey, keyMap, item); // Рекурсивний виклик для вкладених об'єктів
                }
            }
        }
    }

}
