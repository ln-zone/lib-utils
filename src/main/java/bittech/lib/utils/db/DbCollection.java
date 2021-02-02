package bittech.lib.utils.db;

import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.json.JsonBuilder;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DbCollection<T> {

    private String collectionName;
    private Database database;
    private Class<T> clazz;

    private AtomicBoolean autoRecreateCollection = new AtomicBoolean(false);

    public DbCollection(String collectionName, Class<T> clazz, Database database) {
        database.createCollection(collectionName);
        this.collectionName = collectionName;
        this.database = database;
        this.clazz = clazz;
        this.clazz = clazz;
    }

    public void setAutoRecreateCollection(boolean value) {
        this.autoRecreateCollection.set(value);
    }

    public void add(T t) {
        database.saveToDataBase(makeDocument(t), getOrCreateCollection());
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
        return database.getCollectionToClass(getOrCreateCollection(), clazz);
    }

    public T findOne(String key, String value) {
        try {
            return database.findOne(key, value, getOrCreateCollection(), clazz);
        } catch (Exception e) {
            throw new StoredException("Failed to find one", e);
        }
    }

    public List<T> findMany(String key, String value) {
        try {
            return database.findMany(key, value, getOrCreateCollection(), clazz);
        } catch (Exception e) {
            throw new StoredException("Failed to find one", e);
        }
    }

    public void delete(String key, String value) {
        try {
            database.deleteDocument(key, value, getOrCreateCollection());
        } catch (Exception e) {
            throw new StoredException("Failed to delete", e);
        }
    }

    public boolean exists(String key, String value) {
        return database.existObject(key, value, getOrCreateCollection());
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
        return database.getCollection(getOrCreateCollection()).countDocuments();
    }

    public T getLastObject() {
        return database.getLastObjectToClass(getOrCreateCollection(), clazz);
    }

    public void update(String key, String value, T object) {
        delete(key, value);
        add(object);
    }

    private String getOrCreateCollection() {
        if(autoRecreateCollection.get()) {
            database.createCollection(collectionName);
        }
        return collectionName;
    }
}
