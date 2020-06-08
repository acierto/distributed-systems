package com.acierto.distributed.systems.model;

import java.util.HashMap;
import java.util.Map;

public class DocumentData {
    private final Map<String,Double> termToFrequency = new HashMap<>();

    public Double getTermToFrequency(String term) {
        return termToFrequency.get(term);
    }

    public void putTermToFrequency(String term, double frequency) {
        this.termToFrequency.put(term, frequency);
    }
}
