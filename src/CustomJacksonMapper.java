// Create the methods that convert a value from JSON to Java Object
// if the new fields are added method should accept them and set values to this field.

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomJacksonMapper {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var json = "{\n" +
                "   \"id\":1,\n" +
                "   \"firstName\":\"Vadym\",\n" +
                "   \"lastName\":\"Makitra\",\n" +
                "   \"phoneNumber\":\"+38099999999\",\n" +
                "   \"dateOfRegistration\":\"2022-03-20\",\n" +
                "   \"lastActivity\":\"2022-03-21 20:00\",\n" +
                "   \"age\":32,\n" +
                "   \"primitiveInt\":33,\n" +
                "   \"primitiveDouble\":33.2,\n" +
                "   \"primitiveFloat\":45.5,\n" +
                "   \"primitiveLong\":100000,\n" +
                "   \"floatVal\":65.5,\n" +
                "   \"longitude\":34.3,\n" +
                "   \"latitude\":35.6,\n" +
                "   \"email\":\"hmarax3@gmail.com\",\n" +
                "   \"city\":{\n" +
                "      \"name\":\"Lviv\",\n" +
                "      \"longitude\":34.3,\n" +
                "      \"latitude\":35.6\n" +
                "   }\n" +
                "}";

        var user = jsonToObject(json, User.class);

        System.out.println(user);
    }

    private static <T> T jsonToObject(String json, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        if(json.isEmpty()){
            throw new IllegalArgumentException("JSON shouldn't be empty");
        }

        Constructor<T> obj = clazz.getConstructor();
        T convertedObj = obj.newInstance();

        Arrays.stream(clazz.getDeclaredFields()).forEach((field -> {
            String searched = "";

            if (field.getGenericType().getTypeName().equals(String.class.getTypeName())
                    || field.getGenericType().getTypeName().equals(LocalDate.class.getTypeName())) {
                Pattern pattern = Pattern.compile("\\s?\"" + field.getName() + "\":\\s?\"?.\\w+@?\\w+.?\\w+.?\\w+");
                Matcher patternMatcher = pattern.matcher(json);
                if (patternMatcher.find()) {
                    searched = patternMatcher.group(0);
                    searched = searched.split("\\s?\"" + field.getName() + "\"\\s?:\\s?\"")[1];
                }
                try {
                    field.setAccessible(true);
                    if (field.getGenericType().getTypeName().equals(String.class.getTypeName())) {
                        field.set(convertedObj, searched);
                    } else if (field.getGenericType().getTypeName().equals(LocalDate.class.getTypeName())) {
                        field.set(convertedObj, LocalDate.parse(searched, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    } else if (field.getGenericType().getTypeName().equals(LocalDateTime.class.getTypeName())) {
                        field.set(convertedObj, LocalDateTime.parse(searched, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (field.getGenericType().getTypeName().equals(Integer.class.getTypeName())
                    || field.getGenericType().getTypeName().equals(Long.class.getTypeName())
                    || field.getGenericType().getTypeName().equals(Double.class.getTypeName())
                    || field.getGenericType().getTypeName().equals(Float.class.getTypeName())
                    || field.getType().equals(Integer.TYPE)
                    || field.getType().equals(Long.TYPE)
                    || field.getType().equals(Double.TYPE)
                    || field.getType().equals(Float.TYPE)) {
                Pattern pattern = Pattern.compile("\\s?\"" + field.getName() + "\":\\s?[0-9]+");
                Matcher patternMatcher = pattern.matcher(json);
                if (patternMatcher.find()) {
                    searched = patternMatcher.group(0);
                    searched = searched.split("\\s?\"" + field.getName() + "\"\\s?:\\s?")[1];
                }
                try {
                    field.setAccessible(true);
                    if (field.getGenericType().getTypeName().equals(Integer.class.getTypeName())
                        || field.getType().equals(Integer.TYPE)) {
                        field.set(convertedObj, Integer.parseInt(searched));
                    } else if (field.getGenericType().getTypeName().equals(Long.class.getTypeName())
                            || field.getType().equals(Long.TYPE)) {
                        field.set(convertedObj, Long.valueOf(searched));
                    } else if (field.getGenericType().getTypeName().equals(Double.class.getTypeName())
                            || field.getType().equals(Double.TYPE)) {
                        field.set(convertedObj, Double.valueOf(searched));
                    }else if (field.getGenericType().getTypeName().equals(Float.class.getTypeName())
                            || field.getType().equals(Float.TYPE)) {
                        field.set(convertedObj, Float.valueOf(searched));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            } else if (field.getGenericType().getTypeName().equals(LocalDateTime.class.getTypeName())) {
                Pattern pattern = Pattern.compile("\\s?\"" + field.getName() + "\":\\s?\"?.\\w+@?\\w+.?\\w+.?\\w+\\s?\\w+:?\\w?.");
                Matcher patternMatcher = pattern.matcher(json);
                if (patternMatcher.find()) {
                    searched = patternMatcher.group(0);
                    searched = searched.split("\\s?\"" + field.getName() + "\"\\s?:\\s?\"")[1];
                }
                try {
                    field.setAccessible(true);
                    field.set(convertedObj, LocalDateTime.parse(searched, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Pattern pattern = Pattern.compile("\\s?\"" + field.getName() + "\"\\s?:\\s?\\{[\\s\\w\\d\\D]*}");
                Matcher patternMatcher = pattern.matcher(json);
                if (patternMatcher.find()) {
                    searched = patternMatcher.group(0);
                    searched = searched.split("\\s?\"" +field.getName() + "\"\\s?:\\s?")[1];
                }
                try {
                    field.setAccessible(true);
                    field.set(convertedObj ,jsonToObject(searched, field.getType()));
                } catch (NoSuchMethodException
                        | InvocationTargetException
                        | InstantiationException
                        | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }));
        return convertedObj;
    }

    static class User {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String email;
        private Integer age;
        private Long id;
        private LocalDate dateOfRegistration;
        private LocalDateTime lastActivity;
        private Double longitude;
        private Double latitude;
        private Float floatVal;
        private int primitiveInt;
        private double primitiveDouble;
        private float primitiveFloat;
        private long primitiveLong;
        private City city;

        public User() {
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", email='" + email + '\'' +
                    ", age=" + age +
                    ", dateOfRegistration=" + dateOfRegistration.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +
                    ", lastActivity=" + lastActivity.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) +
                    ", longitude=" + longitude +
                    ", latitude=" + latitude +
                    ", floatVal=" + floatVal +
                    ", primitiveInt=" + primitiveInt +
                    ", primitiveDouble=" + primitiveDouble +
                    ", primitiveFloat=" + primitiveFloat +
                    ", primitiveLong=" + primitiveLong +
                    ", city=" + city +
                    '}';
        }

        static class City {
            private String name;
            private float longitude;
            private float latitude;

            public City() {
            }

            @Override
            public String toString() {
                return "City{" +
                        "name='" + name + '\'' +
                        ", longitude=" + longitude +
                        ", latitude=" + latitude +
                        '}';
            }
        }
    }
}
