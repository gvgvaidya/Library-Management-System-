package com.library.services;

import com.library.models.LibraryBranch;
import com.library.repositories.BranchRepository;
import java.util.List;

public class BranchService {
    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public void addBranch(LibraryBranch branch) {
        branchRepository.save(branch);
    }

    public LibraryBranch getBranch(String branchId) {
        return branchRepository.findById(branchId);
    }

    public List<LibraryBranch> listBranches() {
        return branchRepository.findAll();
    }
}
