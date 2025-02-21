package com.example.patchfield_example.data.request;

import com.example.patchfield_example.PatchField;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class UserUpdateRequest {
    private PatchField<String> name = PatchField.notProvided();
    private PatchField<String> email = PatchField.notProvided();

    public UserUpdateRequest() {}

    // Setters
    @JsonSetter(nulls = Nulls.SET)
    public void setName(String name) {
        this.name = PatchField.of(name);
    }

    @JsonSetter(nulls = Nulls.SET)
    public void setEmail(String email) {
        this.email = PatchField.of(email);
    }

    // Getters
    public PatchField<String> getName() {
        return name;
    }

    public PatchField<String> getEmail() {
        return email;
    }
}
