package bittech.lib.utils.db;

import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.json.JsonBuilder;
import org.bson.Document;

import java.util.List;

public class DbCollection<T> {

    private String collectionName;
    private Database database;
    private Class<T> clazz;

    public DbCollection(String collectionName, Class<T> clazz, Database database) {
        database.createCollection(collectionName);
        this.collectionName = collectionName;
        this.database = database;
        this.clazz = clazz;
    }

    public void add(T t) {
        database.saveToDataBase(makeDocument(t), collectionName);
    }

    private Document makeDocument(Object obj) {
        try {
            String json = JsonBuilder.build().toJson(obj);
            return new Document(Document.parse(json));
        } catch (Exception e) {
            throw new StoredException("Failed to create document", e);
        }
    }

    public List<T> listAll() {
        return database.getCollectionToClass(collectionName, clazz);
    }

    public T findOne(String key, String value) {
        try {
            return database.findOne(key, value, collectionName, clazz);
        } catch (Exception e) {
            throw new StoredException("Failed to find one", e);
        }
    }

    public List<T> findMany(String key, String value) {
        try {
            return database.findMany(key, value, collectionName, clazz);
        } catch (Exception e) {
            throw new StoredException("Failed to find one", e);
        }
    }

    public void delete(String key, String value) {
        try {
            database.deleteDocument(key, value, collectionName);
        } catch (Exception e) {
            throw new StoredException("Failed to delete", e);
        }
    }

    public boolean exists(String key, String value) {
        return database.existObject(key, value, collectionName);
    }

    public void moveOne(String key, String value, DbCollection<T> toCollection) {
        try {
            T el = findOne(key, value);
            toCollection.add(el);
            delete(key, value);
        } catch (Exception e) {
            throw new StoredException("Failed move element between collections", e);
        }
    }

    public long getColletionSize() {
        return database.getCollection(collectionName).countDocuments();
    }

    public T getLastObject() {
        return database.getLastObjectToClass(collectionName, clazz);
    }

    public void update(String key, String value, T object) {
        delete(key, value);
        add(object);
    }
}