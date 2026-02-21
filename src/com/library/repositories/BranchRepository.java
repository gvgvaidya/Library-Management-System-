package com.library.repositories;

import com.library.models.LibraryBranch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchRepository {
    private final Map<String, LibraryBranch> branches = new HashMap<>();

    public void save(LibraryBranch branch) {
        branches.put(branch.getBranchId(), branch);
    }

    public LibraryBranch findById(String branchId) {
        return branches.get(branchId);
    }

    public List<LibraryBranch> findAll() {
        return new ArrayList<>(branches.values());
    }

    public boolean exists(String branchId) {
        return branches.containsKey(branchId);
    }
}
