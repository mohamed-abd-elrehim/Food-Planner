package com.example.mealmate.model;

public class Step {
    private String title;
    private String instruction;

    public Step(String title, String instruction) {
        this.title = title;
        this.instruction = instruction;
    }

    public String getTitle() {
        return title;
    }

    public String getInstruction() {
        return instruction;
    }
}
