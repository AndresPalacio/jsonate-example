import com.samskivert.mustache.Escapers;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JmustacheTest {

    @Test
    public void test01(){
        String text = "One, two, {{second}}. Three sir!";
        Template tmpl = Mustache.compiler().compile(text);
        Map<String, Object> data = new HashMap<>();
        data.put("three", "five");
        Map<String, Object> data2 = new HashMap<>();
        data2.put("test", "test");
        data.put("second", data2);
        System.out.println(tmpl.execute(data));
    }

    @Test
    public void test02(){
        String tmpl = "{{#persons}}{{name}}: {{age}}\n{{/persons}}";
        String tmplSecond = "name: {{#persons}}{{name}}\n" +
                "lastName: {{data}}"+
                "{{/persons}}";
        Map<String, Object> data = new HashMap<>();
        data.put("lastName", "Doe");
        data.put("firstName", "John");
        Map<String,Object> otherData = new HashMap<>();
        otherData.put("country","CO");
        data.put("otherData",otherData);
        Person person = new Person("Elvis",data);
        String result=  Mustache.compiler().compile(tmplSecond).execute(new Object() {
            Object persons = person;
        });
        System.out.println(result);
    }

    class Person {
        public final String name;
        Map<String, Object> data = new HashMap<>();

        public Person (String name,Map<String, Object> data) {
            this.name = name;
            this.data = data;
        }
        public Person (String name, int age) {
            this.name = name;
            _age = age;
        }
        public int getAge () {
            return _age;
        }
        protected int _age;
    }

    @Test
    public void test03(){
        final String templateDir = "src\\main\\resources";
        Mustache.Compiler c = Mustache.compiler().withLoader(name -> new FileReader(new File(templateDir, name)));
        String tmpl = "...{{>subtmpl.txt}}...";
        Map<String, String> data = new HashMap<>();
        data.put("bb", "666");
        String s=c.compile(tmpl).execute(data);
        System.out.println(s);
    }

    @Test
    public void test04() throws IOException {
        final String templateDir = "src\\main\\resources";
        Mustache.Compiler c = Mustache.compiler().withLoader(name -> new FileReader(new File(templateDir, name)));
        String tmpl = "{{>helloword.java}}";
        Map<String, String> data = new HashMap<>();
        data.put("a", "kenshine");
        String content=c.compile(tmpl).execute(data);
        System.out.println(content);
        FileWriter fileWriter = new FileWriter("F:\\IDEAworkespace\\codedemo\\springbootdemos05\\springbootdemo435-Jmustache\\src\\main\\java\\com\\kenshine\\jmustache\\HelloWord.java");
        fileWriter.write(content);
        fileWriter.close();
    }


    @Test
    public void test05(){
        String tmpl = "{{#bold}}{{name}} is awesome.{{/bold}}";
        String s=Mustache.compiler().compile(tmpl).execute(new Object() {
            String name = "Willy";
            Mustache.Lambda bold = (frag, out) -> {
                out.write("<b>");
                frag.execute(out);
                out.write("</b>");
            };
        });
        System.out.println(s);
    }

    @Test
    public void test06(){
        String tmpl = "{{exists}} {{nullValued}} {{doesNotExist}}?";
        String s=Mustache.compiler().defaultValue("what").compile(tmpl).execute(new Object() {
            String exists = "Say";
            String nullValued = null;
            // String doesNotExist
        });
        System.out.println(s);
    }

    @Test
    public void test07(){
        String s=Mustache.compiler().escapeHTML(false).compile("{{foo}}").execute(new Object() {
            String foo = "<bar>";
        });
        System.out.println(s);
    }

    @Test
    public void test08(){
       String s= Mustache.compiler().withFormatter(new Mustache.Formatter() {
            @Override
            public String format (Object value) {
                if (value instanceof Date){
                    return _fmt.format((Date)value);
                } else {
                    return String.valueOf(value);
                }
            }
            protected DateFormat _fmt = new SimpleDateFormat("yyyy/MM/dd");
        }).compile("{{msg}}: {{today}}").execute(new Object() {
            String msg = "Date";
            Date today = new Date();
        });
        System.out.println(s);
    }

    @Test
    public void test09(){
        String[][] escapes = {{ "[", "[[" }, { "]", "]]" }};
        String s=Mustache.compiler().withEscaper(Escapers.simple(escapes)).
                compile("{{foo}}").execute(new Object() {
            String foo = "[bar]";
        });
        System.out.println(s);
    }



}
