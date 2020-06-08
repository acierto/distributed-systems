package com.acierto.distributed.systems.search;

import com.acierto.distributed.systems.model.DocumentData;

import java.util.*;

public class TFIDF {
    public static double calculateTermFrequency(List<String> words, String term) {
        long count = 0;
        for (String word : words) {
            if (word.equalsIgnoreCase(term)) {
                count++;
            }
        }
        return (double) count / words.size();
    }

    public static DocumentData createDocumentData(List<String> words, List<String> terms) {
        DocumentData documentData = new DocumentData();

        for (String term : terms) {
            double frequency = calculateTermFrequency(words, term);
            documentData.putTermToFrequency(term, frequency);
        }

        return documentData;
    }

    private static double getInverseDocumentFrequency(String term, Map<String, DocumentData> documentResults) {
        double nt = 0;

        for (String document : documentResults.keySet()) {
            DocumentData documentData = documentResults.get(document);
            double frequency = documentData.getTermToFrequency(term);
            if (frequency > 0.0) {
                nt++;
            }
        }

        return nt == 0 ? 0 : Math.log10(documentResults.size() / nt);
    }

    private static Map<String, Double> getTermToInverseDocumentFrequencyMap(List<String> terms,
                                                                            Map<String, DocumentData> documentResults) {
        Map<String, Double> termToIdf = new HashMap<>();
        for (String term : terms) {
            double idf = getInverseDocumentFrequency(term, documentResults);
            termToIdf.put(term, idf);
        }
        return termToIdf;
    }

    private static double calculateDocumentScore(List<String> terms,
                                                 DocumentData documentData,
                                                 Map<String, Double> termToInverseDocumentFrequency) {
        double score = 0;
        for (String term : terms) {
            double termFrequency = documentData.getTermToFrequency(term);
            double itf = termToInverseDocumentFrequency.get(term);
            score += termFrequency * itf;
        }
        return score;
    }

    public static Map<Double, List<String>> getDocumentsSortedByScore(List<String> terms,
                                                                      Map<String, DocumentData> documentResults) {
        TreeMap<Double, List<String>> scoreToDocuments = new TreeMap<>();
        Map<String, Double> termToInverseDocumentFrequency = getTermToInverseDocumentFrequencyMap(terms, documentResults);

        for (String document : documentResults.keySet()) {
            DocumentData documentData = documentResults.get(document);
            double score = calculateDocumentScore(terms, documentData, termToInverseDocumentFrequency);

            addDocumentScoreToTreeMap(scoreToDocuments, score, document);
        }
        return scoreToDocuments.descendingMap();
    }

    private static void addDocumentScoreToTreeMap(TreeMap<Double, List<String>> scoreToDocuments, double score, String document) {
        List<String> documentsWithCurrentScore = scoreToDocuments.get(score);
        if (documentsWithCurrentScore == null) {
            documentsWithCurrentScore = new ArrayList<>();
        }
        documentsWithCurrentScore.add(document);
        scoreToDocuments.put(score, documentsWithCurrentScore);
    }

    public static List<String> getWordsFromLine(String line) {
        return Arrays.asList(line.split("(\\.)+|(,)+|( )+|(-)+(\\?)+|(!)+|(;)+|(:)+|(/d)+|(/n)+"));
    }

    public static List<String> getWordsFromLines(List<String> lines) {
        List<String> words = new ArrayList<>();
        for (String line : lines) {
            words.addAll(getWordsFromLine(line));
        }
        return words;
    }
}
