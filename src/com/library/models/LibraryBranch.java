package com.library.models;

public class LibraryBranch {
    private final String branchId;
    private String name;
    private String location;
    private boolean open;

    public LibraryBranch(String branchId, String name, String location, boolean open) {
        this.branchId = branchId;
        this.name = name;
        this.location = location;
        this.open = open;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
