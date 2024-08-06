import com.api.jsonata4java.expressions.EvaluateException;
import com.api.jsonata4java.expressions.Expressions;
import com.api.jsonata4java.expressions.ParseException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonNata {

    private final ObjectMapper objectMapper;

    public static final String COMPLEX_EXPRESSION_JSONATA = "{ \"name_first\": name, \"age_example\": age }";

    public JsonNata() {
        this.objectMapper = new ObjectMapper();
    }

    public <T> String getJsonStringFromDto(T dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Expressions getJsonataExpression(String expression) {
        try {
            return Expressions.parse(expression);
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode evaluateJsonataExpression(Expressions expression, JsonNode jsonNode) {
        try {
            return expression.evaluate(jsonNode);
        } catch (EvaluateException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode getJsonNodeFromJsonString(String dataString) {
        try {
            return objectMapper.readTree(dataString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class Person {
        public final String name;
        public final int age;

        public Person (String name, int age) {
            this.name = name;
            this.age = age;
        }

    }


    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static List<String> listFields(Object obj) {
        List<String> fieldMappings = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        List<Field> fields = getAllFields(clazz);

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                fieldMappings.add(field.getName() + ": " + value);
            } catch (IllegalAccessException e) {
                fieldMappings.add(field.getName() + ": " + "access denied");
            }
        }
        return fieldMappings;
    }

    public static Map<String, Object> generateFieldMap(Object obj) {
        Map<String, Object> fieldMap = new HashMap<>();
        Class<?> clazz = obj.getClass();
        List<Field> fields = getAllFields(clazz);

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                fieldMap.put(field.getName(), value != null ? value : null);
            } catch (IllegalAccessException e) {
                fieldMap.put(field.getName(), "access denied");
            } catch (Exception e) {
                fieldMap.put(field.getName(), "[ERROR: " + e.getMessage() + "]");
            }
        }

        return fieldMap;
    }
    public static JsonNode createJsonTree(Map<String, Object> map) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
        mapper.enable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL);
        ObjectNode rootNode = mapper.createObjectNode();
        map.forEach((key, value) -> rootNode.set(key, mapper.valueToTree(value)));
        return rootNode;
    }
    @Test
    public void test01() {
        Person person = new Person("Elvis", 120);
        String result = getJsonStringFromDto(person);
        JsonNode data = getJsonNodeFromJsonString(result);
        Expressions expression = getJsonataExpression(COMPLEX_EXPRESSION_JSONATA);
        JsonNode resultNode = evaluateJsonataExpression(expression, data);
        System.out.println(resultNode);

        Map<String, Object> fieldMap = generateFieldMap(person);
        JsonNode jsonNode = createJsonTree(fieldMap);
        JsonNode resultNodeSecond = evaluateJsonataExpression(expression, jsonNode);
        System.out.println(resultNodeSecond);
    }
}
