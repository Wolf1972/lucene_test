package ru.bis.example.model;

import ru.bis.example.index.MessageToDocument;
import org.apache.lucene.document.Document;

public class Message {
    private String purpose;
    private String title;
    private String account; // Yet another field

    public Document convertToDocument() {
        return MessageToDocument.createWith(title, purpose, account);
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
