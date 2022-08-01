// Create the methods that convert a value from JSON to Java Object
// if the new fields are added method should accept them and set values to this field.

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomJacksonMapper {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var json = "{\n" +
            "  \"firstName\": \"Vadym\",\n" +
            "  \"lastName\": \"Makitra\",\n" +
            "  \"phoneNumber\": \"+38099999999\",\n" +
            "  \"email\": \"hmarax3@gmail.com\"\n" +
            "}";

        var user = jsonToObject(json, User.class);
        System.out.println(user);
    }

    private static <T> T jsonToObject(String json, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> obj = clazz.getConstructor();
        T convertedObj = obj.newInstance();
        Arrays.stream(clazz.getDeclaredFields()).forEach((field -> {
            Pattern pattern = Pattern.compile("\\s?\"" + field.getName() +"\":\\s?\".\\w+@?\\w+.?\\w+");
            Matcher patternMatcher = pattern.matcher(json);
            String searched = "";
            if (patternMatcher.find()) {
                searched = patternMatcher.group(0);
                searched = searched.split("\\s?\"" + field.getName() + "\"\\s?:\\s?\"")[1];
            }
            try {
                field.setAccessible(true);
                field.set(convertedObj, searched);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }));
        return convertedObj;
    }

    static class User {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String email;

        public User() {
        }

        @Override
        public String toString() {
            return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
        }
    }
}
