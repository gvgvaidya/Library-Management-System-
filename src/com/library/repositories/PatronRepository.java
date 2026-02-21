package com.library.repositories;

import com.library.models.Patron;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatronRepository {
    private final Map<String, Patron> patrons = new HashMap<>();

    public void save(Patron patron) {
        patrons.put(patron.getPatronId(), patron);
    }

    public Patron findById(String patronId) {
        return patrons.get(patronId);
    }

    public List<Patron> findAll() {
        return new ArrayList<>(patrons.values());
    }

    public void delete(String patronId) {
        patrons.remove(patronId);
    }

    public boolean exists(String patronId) {
        return patrons.containsKey(patronId);
    }
}
