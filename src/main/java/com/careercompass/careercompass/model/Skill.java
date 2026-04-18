package com.careercompass.careercompass.model;

public class Skill implements Comparable <Skill> {

    private String name;
    private int demandCount;
    private String category;

    public Skill(String name, int demandCount, String category){
        this.name = name;
        this.demandCount = demandCount;
        this.category = category;
    }

    // sorting skills by demand - highest first
    @Override
    public int compareTo(Skill other){
        return Integer.compare(other.demandCount, this.demandCount);
    }

    // Getters
    public String getName(){return name; }
    public int getDemandCount(){return demandCount; }
    public String getCategory(){return category; }
    public void setDemandCount(int demandCount){this.demandCount = demandCount; }
}
