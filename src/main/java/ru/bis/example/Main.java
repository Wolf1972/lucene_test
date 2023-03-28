package ru.bis.example;

import ru.bis.example.index.MessageIndexer;
import ru.bis.example.index.MessageToDocument;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        System.out.println("Lucene example.");

        final MessageIndexer indexer = new MessageIndexer("/tmp/teaser_index");

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Choose operation with index:");
        System.out.println("0 - none (by default)");
        System.out.println("1 - append");
        System.out.println("2 - create");
        System.out.print("?: ");
        String operationStr = console.readLine();

        long startTime = System. currentTimeMillis();

        if (operationStr.equals("1") || operationStr.equals("2")) {
            String opStart;
            String opEnd;
            if (operationStr.equals("1")) {
                opStart = "append";
                opEnd = "appended";
            }
            else {
                opStart = "create";
                opEnd = "created";
            }
            System.out.println("Operation with index: " + opStart);
            System.out.print("Source file: ");
            String sourceFile = console.readLine();
            FileReader file = new FileReader(sourceFile);
            BufferedReader reader = new BufferedReader(file);
            String line = null;
            int lineCount = 0;
            do {
                line = reader.readLine();
                if (line == null) break;
                Document teaserDoc = MessageToDocument.createWith(Integer.toString(lineCount), line, "4081781010000" + String.format("%07d", lineCount));
                indexer.index((lineCount == 0) && (operationStr.equals("2")), teaserDoc); // first iteration index created, then updated only
                lineCount++;
                if ((lineCount % 1000) == 0) System.out.println(lineCount + " lines indexed in " + ((System.currentTimeMillis() - startTime) / 1000) + " s.");
            }
            while (true);
            System.out.println("Index " + opEnd + " for " + lineCount + " string(s) in " + ((System.currentTimeMillis() - startTime) / 1000) + " s.");
        }

        final SearchVariants search = new SearchVariants(indexer.readIndex());
        String toSearch = null;
        do {
            System.out.print("Query (empty string for exit):\t");
            toSearch = console.readLine();
            if (toSearch == null || toSearch.isEmpty()) break;
            startTime = System.currentTimeMillis();
//            search.fuzzySearch(toSearch); // Only fuzzy search
            search.searchWithQuery(toSearch);  // Search with query
            System.out.println("Query execution time: " + (System. currentTimeMillis() - startTime) + " ms.");
        }
        while (true);

    }
}
