package com.library.repositories;

import com.library.models.BorrowingRecord;
import java.util.ArrayList;
import java.util.List;

public class BorrowingRecordRepository {
    private final List<BorrowingRecord> records = new ArrayList<>();

    public void save(BorrowingRecord record) {
        records.add(record);
    }

    public List<BorrowingRecord> findAll() {
        return new ArrayList<>(records);
    }

    public List<BorrowingRecord> findActive() {
        List<BorrowingRecord> active = new ArrayList<>();
        for (BorrowingRecord record : records) {
            if (record.getReturnDate() == null) {
                active.add(record);
            }
        }
        return active;
    }

    public List<BorrowingRecord> findByPatron(String patronId) {
        List<BorrowingRecord> results = new ArrayList<>();
        for (BorrowingRecord record : records) {
            if (record.getPatronId().equalsIgnoreCase(patronId)) {
                results.add(record);
            }
        }
        return results;
    }

    public BorrowingRecord findActiveByBookIsbn(String isbn) {
        for (BorrowingRecord record : records) {
            if (record.getBookIsbn().equalsIgnoreCase(isbn) && record.getReturnDate() == null) {
                return record;
            }
        }
        return null;
    }
}
