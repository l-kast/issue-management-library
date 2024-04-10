package org.example.model;

public record Service(String id, String name) {
    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
