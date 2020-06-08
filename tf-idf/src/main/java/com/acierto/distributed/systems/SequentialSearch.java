package com.acierto.distributed.systems;

import com.acierto.distributed.systems.model.DocumentData;
import com.acierto.distributed.systems.search.TFIDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SequentialSearch {
    private static final String BOOKS_DIRECTORY = "/books";
    private static final String SEARCH_QUERY_1 = "The best detective that catches many criminals using his deductive methods";
    private static final String SEARCH_QUERY_2 = "The girl that falls through a rabbit hole into a fantasy wonderland";
    private static final String SEARCH_QUERY_3 = "A war between Russia and France in the cold winter";

    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
        URI bookDirectoryUri = SequentialSearch.class.getResource(BOOKS_DIRECTORY).toURI();
        File documentsDirectory = Paths.get(bookDirectoryUri).toFile();

        if (documentsDirectory.exists()) {
            String[] files = documentsDirectory.list();
            if (files != null) {
                List<String> documents = Arrays.stream(files)
                        .map(documentName -> bookDirectoryUri.getPath() + "/" + documentName)
                        .collect(Collectors.toList());

                List<String> terms = TFIDF.getWordsFromLine(SEARCH_QUERY_1);
                findMostRelevantDocuments(documents, terms);
            }
        }
    }

    private static void findMostRelevantDocuments(List<String> documents, List<String> terms) throws FileNotFoundException {
        Map<String, DocumentData> documentsResults = new HashMap<>();

        for (String document : documents) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(document));
            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            List<String> words = TFIDF.getWordsFromLines(lines);
            DocumentData documentData = TFIDF.createDocumentData(words, terms);
            documentsResults.put(document, documentData);
        }

        Map<Double, List<String>> documentsByScore = TFIDF.getDocumentsSortedByScore(terms, documentsResults);
        printResults(documentsByScore);
    }

    private static void printResults(Map<Double, List<String>> documentsByScore) {
        for (Map.Entry<Double, List<String>> docScorePair : documentsByScore.entrySet()) {
            double score = docScorePair.getKey();
            for (String document : docScorePair.getValue()) {
                String bookName = Paths.get(document).getFileName().toString();
                System.out.println(String.format("Book : %s - score: %f", bookName, score));
            }
        }
    }
}
