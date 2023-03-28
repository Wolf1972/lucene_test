package ru.bis.example.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * We will use this class to convert messages to Lucene documents
 */
public class MessageToDocument {

    /**
     * Creates Lucene Document using three strings:  title, purpose & account
     *
     * @return resulted document
     */
    public static Document createWith(final String titleStr, final String purposeStr, final String accountStr) {
        final Document document = new Document();

        final FieldType textIndexedType = new FieldType();
        textIndexedType.setStored(true);
        textIndexedType.setIndexOptions(IndexOptions.DOCS);
        textIndexedType.setTokenized(true);

        // title field (sample title with counter)
        Field title = new Field("title", titleStr, textIndexedType);
        // payment purpose
        Field purpose = new Field("purpose", purposeStr, textIndexedType);
        // sample 20-digits account for example)
        Field account = new Field("account", accountStr, textIndexedType);

        document.add(title);
        document.add(purpose);
        document.add(account);

        return document;
    }
}
