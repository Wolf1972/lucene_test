package ru.bis.example;

import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import java.io.IOException;

public class SearchVariants {
    public static final int DEFAULT_LIMIT = 10;
    private final IndexReader reader;

    public SearchVariants(IndexReader reader) {
        this.reader = reader;
    }

    /**
     * Search using TermQuery
     * @param toSearch string to search
     * @param searchField field where to search. We have "title", "purpose" and "account" fields
     * @param limit how many results to return
     * @throws IOException
     * @throws ParseException
     */
    public void searchIndexWithTermQuery(final String toSearch, final String searchField, final int limit) throws IOException, ParseException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        final Term term = new Term(searchField, toSearch);
        final Query query = new TermQuery(term);
        final TopDocs search = indexSearcher.search(query, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /**
     * This is wrapper to searchIndexWithTermQuery
     * It executes searchIndexWithTermQuery using "purpose" field and limiting to 10 results
     *
     * @param toSearch string to search in the "purpose" field
     * @throws IOException
     * @throws ParseException
     */
    public void searchIndexWithTermQueryByPurpose(final String toSearch) throws IOException, ParseException {
        searchIndexWithTermQuery(toSearch, "purpose", DEFAULT_LIMIT);
    }

    /**
     * Search using QueryParser
     * @param toSearch string to search
     * @param limit how many results to return
     * @throws IOException
     * @throws ParseException
     */
    public void searchWithQuery(final String toSearch, final int limit) throws IOException, ParseException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        final QueryParser queryParser = new QueryParser("purpose", new RussianAnalyzer());
        final Query query = queryParser.parse(toSearch);
        System.out.println("Type of query: " + query.getClass().getSimpleName());

        final TopDocs search = indexSearcher.search(query, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /**
     * This is wrapper to searchWithQuery function
     * it executes searchWithQuery with default limiting to 10 results
     *
     * @param toSearch
     * @throws IOException
     * @throws ParseException
     */
    public void searchWithQuery(final String toSearch) throws IOException, ParseException {
        searchWithQuery(toSearch, DEFAULT_LIMIT);
    }

    /**
     * Search using FuzzyQuery.
     * @param toSearch string to search
     * @param searchField field where to search. We have "title", "purpose" and "account" fields
     * @param limit how many results to return
     * @throws IOException
     * @throws ParseException
     */
    public void fuzzySearch(final String toSearch, final String searchField, final int limit) throws IOException, ParseException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        final Term term = new Term(searchField, toSearch);

        final int maxEdits = 2; // This is very important variable. It regulates fuzziness of the query
        final Query query = new FuzzyQuery(term, maxEdits);
        final TopDocs search = indexSearcher.search(query, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /**
     * Wrapper to fuzzySearch function.
     * It executed fuzzySearch with default limit and "purpose" field as target field
     *
     * @param toSearch string to search
     * @throws IOException
     * @throws ParseException
     */
    public void fuzzySearch(final String toSearch) throws IOException, ParseException {
        fuzzySearch(toSearch, "purpose", DEFAULT_LIMIT);
    }

    private void showHits(final ScoreDoc[] hits) throws IOException {
        if (hits.length == 0) {
            System.out.println("\n\tLucene: no results found.");
            return;
        }
        System.out.println("\n\tLucene found:");
        for (ScoreDoc hit : hits) {
            final String title = reader.document(hit.doc).get("title");
            final String purpose = reader.document(hit.doc).get("purpose");
            final String account = reader.document(hit.doc).get("account");
            System.out.println("\n\tDocument Id = " + hit.doc + ", title = '" + title + "' account = '" + account + "'" +
                               "\n\tpurpose = '" + purpose + "'");
        }
    }
}
