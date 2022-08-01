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
            "  \"id\": 1,\n" +
            "  \"firstName\": \"Vadym\",\n" +
            "  \"lastName\": \"Makitra\",\n" +
            "  \"phoneNumber\": \"+38099999999\",\n" +
            "  \"dateOfRegistration\": \"2022-03-20\",\n" +
            "  \"lastActivity\": \"2022-03-21 20:00\",\n" +
            "  \"age\": 32,\n" +
            "  \"longitude\": 34.3,\n" +
            "  \"latitude\": 35.6,\n" +
            "  \"email\": \"hmarax3@gmail.com\"\n" +
            "}";

        var user = jsonToObject(json, User.class);

        System.out.println(user);
    }

    private static <T> T jsonToObject(String json, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> obj = clazz.getConstructor();
        T convertedObj = obj.newInstance();
        Arrays.stream(clazz.getDeclaredFields()).forEach((field -> {
            String searched = "";
            if (field.getGenericType().getTypeName().equals(String.class.getTypeName()) || field.getGenericType().getTypeName().equals(LocalDate.class.getTypeName())) {
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
                || field.getGenericType().getTypeName().equals(Double.class.getTypeName())) {
                Pattern pattern = Pattern.compile("\\s?\"" + field.getName() + "\":\\s?[0-9]+");
                Matcher patternMatcher = pattern.matcher(json);
                if (patternMatcher.find()) {
                    searched = patternMatcher.group(0);
                    searched = searched.split("\\s?\"" + field.getName() + "\"\\s?:\\s?")[1];
                }
                try {
                    field.setAccessible(true);
                    if (field.getGenericType().getTypeName().equals(Integer.class.getTypeName())) {
                        field.set(convertedObj, Integer.parseInt(searched));
                    } else if (field.getGenericType().getTypeName().equals(Long.class.getTypeName())) {
                        field.set(convertedObj, Long.valueOf(searched));
                    } else if (field.getGenericType().getTypeName().equals(Double.class.getTypeName())) {
                        field.set(convertedObj, Double.valueOf(searched));
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
                '}';
        }
    }
}