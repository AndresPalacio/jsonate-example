import com.samskivert.mustache.Mustache;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class JmustacheTest02 {

    @Test
    public void test01(){
       String s1= Mustache.compiler().compile("{{this}}").execute("hello");
       String s2=Mustache.compiler().compile("{{#names}}{{this}}{{/names}}").execute(new Object() {
            List<String> names () { return Arrays.asList("Tom", "Dick", "Harry"); }
        });
        System.out.println(s1);
        System.out.println(s2);
    }

    @Test
    public void test02(){
        Mustache.compiler().compile("{{.}}").execute("hello");
        String s=Mustache.compiler().compile("{{#names}}{{.}}{{/names}}").execute(new Object() {
            List<String> names () { return Arrays.asList("Tom", "Dick", "Harry"); }
        });
        System.out.println(s);
    }

    @Test
    public void test03(){
        String tmpl = "{{#things}}{{^-first}}, {{/-first}}{{this}}{{/things}}";
        String s=Mustache.compiler().compile(tmpl).execute(new Object() {
            List<String> things = Arrays.asList("one", "two", "three");
        });
        System.out.println(s);
    }

    @Test
    public void test04(){
        String tmpl = "My favorite things:\n{{#things}}{{-index}}. {{this}}\n{{/things}}";
        String s=Mustache.compiler().compile(tmpl).execute(new Object() {
            List<String> things = Arrays.asList("Peanut butter", "Pen spinning", "Handstands");
        });
        System.out.println(s);
    }

    @Test
    public void test05(){
        String s=Mustache.compiler().compile("Hello {{field.who}}!").execute(new Object() {
            public Object field = new Object() {
                public String who () { return "world"; }
            };
        });
        System.out.println(s);
    }


    @Test
    public void test06(){
        String s=Mustache.compiler().compile("Hello {{class.name}}!").execute(new Object());
        System.out.println();
    }

    @Test
    public void test07(){
        String template = "{{outer}}:\n{{#inner}}{{outer}}.{{this}}\n{{/inner}}";
        String s=Mustache.compiler().compile(template).execute(new Object() {
            String outer = "foo";
            List<String> inner = Arrays.asList("bar", "baz", "bif");
        });
        System.out.println(s);
    }

}
