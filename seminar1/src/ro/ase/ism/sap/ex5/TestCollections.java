package ro.ase.ism.sap.ex5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

class User {
    public int id;
    public String username;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    @Override
    public String toString() {
        return this.username + "-" + this.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                username.equals(user.username);
    }
}

public class TestCollections {

    public static void main(String[] args) {
        // lists
        ArrayList<Integer> values = new ArrayList<>();
        values.add(10);
        values.add(0, 12);
        for (int value : values) {
            System.out.print(value + " ");
        }
        System.out.println();

        // sets
        HashSet<Integer> uniqueValues = new HashSet<>();
        uniqueValues.add(10);
        uniqueValues.add(10);
        uniqueValues.add(11);
        uniqueValues.add(12);
        for (int value : uniqueValues) {
            System.out.print(value + " ");
        }
        System.out.println();

        // maps - dictionary
        HashMap<Integer, String> hashMap = new HashMap<>();
        hashMap.put(1, "John");
        hashMap.put(2, "Mary");
        hashMap.put(2, "Alice");
        hashMap.forEach((integer, s) -> {
            System.out.println("Username " + integer + " is " + s);
        });

        // saving user-defined objects
        HashSet<User> dbUsers = new HashSet<>();
        dbUsers.add(new User(1, "John"));
        dbUsers.add(new User(2, "Alice"));
        dbUsers.add(new User(1, "John"));

        dbUsers.forEach(System.out::println);
    }
}
