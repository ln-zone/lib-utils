package bittech.lib.utils.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

import bittech.lib.utils.Config;
import bittech.lib.utils.Utils;
import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.json.JsonBuilder;
import bittech.lib.utils.json.RawJson;

public class Database implements AutoCloseable {
    private static final String MIX_COLLECTION_NAME = "mix";

    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;

    public Database(String uriStr, String dbName) {
        try {
            MongoClientURI uri = new MongoClientURI(uriStr);
            CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries
                            .fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            mongoClient = new MongoClient(uri);
            mongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(pojoCodecRegistry);
            if (!collectionExists(MIX_COLLECTION_NAME)) {
                createCollection(MIX_COLLECTION_NAME);
            }
            System.out.println("Database has been initialized ! ");
        } catch (Exception ex) {
            throw new StoredException("Failed to init db", ex);
        }
    }

    public Database() {
        this(Config.getInstance().getEntry("mongodbUri", String.class),
                Config.getInstance().getEntry("mongodbName", String.class));
    }

    public boolean dbInit() { // TODO: Ogolna wartosc
        return getOrAddEntry("isInitializedTokensList", false, Boolean.class, "mix");
    }

//    private void createIndex(String collection, String key) {
//        mongoDatabase.getCollection(collection).listIndexes();
//        mongoDatabase.getCollection(collection).createIndex(Indexes.ascending(key));
//    }

    public void deleteCollection(String collection) {
        mongoDatabase.getCollection(collection).drop();
    }

//    protected List<Document> search(String collection, String key, String value) {
//        createIndex(collection, key);
//        List<Document> docs = new LinkedList<>();
//        Document document = new Document(key, value);
//        for (Document doc : mongoDatabase.getCollection(collection).find(document)) {
//            docs.add(doc);
//        }
//        return docs;
//    }

//    protected Document searchOne(String collection, String key, String value) {
//        List<Document> list = search(collection, key, value);
//        if (list.size() != 1) {
//            throw new StoredException("Should be one document, but is: " + list.size() + " document", null);
//        }
//        return list.get(0);
//    }

    public static void connectAndClean(String uri, String name) {
        try (Database database = new Database(uri, name)) {
            database.clean();
        }
    }

    public void clean() {
        try {
            for (String name : mongoDatabase.listCollectionNames()) {
                mongoDatabase.getCollection(name).drop();
            }
        } catch (Exception e) {
            throw new StoredException("Failed to delete date from database ", e);
        }

    }

    public MongoCollection<Document> getCollection(String nameOfCollection) {
        try {
            return mongoDatabase.getCollection(nameOfCollection);
        } catch (Exception e) {
            throw new StoredException("Failed to collection " + nameOfCollection, e);
        }
    }

    public <T> T getEntry(String id, Class<T> clazz) {
        try {
            Document document = new Document();
            document.put("_id", id);
            Document document1 = getCollection("mix").find(document).first();
            String strJson = document1.toJson();
            Wrapper myJson = JsonBuilder.build().fromJson(strJson, Wrapper.class);
            return myJson.value.fromJson(clazz);
        } catch (Exception ex) {
            throw new StoredException("Failed to get entry for id " + id, ex);
        }
    }

    public boolean collectionExists(String collectionName) {
        try {
            for (String nameCollection : mongoDatabase.listCollectionNames()) {
                if (nameCollection.equals(collectionName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new StoredException("failed to check collection exists: " + collectionName, e);
        }
    }

    public void createCollection(String nameOfCollection) {
        try {
            if (!collectionExists(nameOfCollection)) {
                mongoDatabase.createCollection(nameOfCollection);
            }
        } catch (Exception e) {
            throw new StoredException("failed to create collection: " + nameOfCollection, e);
        }
    }

    protected void deleteDocument(String key, String value, String nameOfCollection) {
        try {
            mongoDatabase.getCollection(nameOfCollection).deleteOne(new Document(key, value));
        } catch (Exception e) {
            throw new StoredException(
                    "Failed to delete document from " + nameOfCollection + " key " + key + " value " + value, e);
        }
    }

    public boolean existObject(String key, String value, String nameOfCollection) {
        try {
            MongoCollection<Document> collectionToDeleteDocument = mongoDatabase.getCollection(nameOfCollection);
            for (Document document : collectionToDeleteDocument.find()) {
                if (document.get(key).equals(value)) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            throw new StoredException("Failed to check if object exist for key: " + key + " and value " + value
                    + "in collection " + nameOfCollection, ex);
        }
    }

    public boolean isEntryExists(String id) {
        return existObject("_id", id, MIX_COLLECTION_NAME);
    }

    protected void assertEntryExists(String id, String collectionName) {
        if (!existObject("_id", id, collectionName)) {
            throw new StoredException(
                    "Such entry do not exist with id: " + id + " in collection " + MIX_COLLECTION_NAME, null);
        }
    }

//    protected void moveAndDeleteDocument(String key, String value, String deleteFrom, String moveTo) {
//        try {
//            MongoCollection<Document> collectionToDeleteDocument = mongoDatabase.getCollection(deleteFrom);
//            MongoCollection<Document> collectionToMoveDocument = mongoDatabase.getCollection(moveTo);
//            Document documentToMove = new Document();
//            for (Document document : collectionToDeleteDocument.find()) {
//                if (document.get(key).equals(value)) {
//                    documentToMove = document;
//                    collectionToDeleteDocument.deleteOne(document);
//                }
//            }
//            collectionToMoveDocument.insertOne(documentToMove);
//        } catch (Exception e) {
//            throw new StoredException("Failed to move and delete document from " + deleteFrom + " to " + moveTo
//                    + " key " + key + " value " + value, e);
//
//        }
//    }

    protected void assertCollectionExists(String nameOfCollection) throws Exception {
        if (!collectionExists(nameOfCollection)) {
            throw new Exception("Such collection do not exists: " + nameOfCollection);
        }
    }

    public void saveToDataBase(Document document, String nameOfCollection) {
        try {
            assertCollectionExists(nameOfCollection);
            MongoCollection<Document> collection = mongoDatabase.getCollection(nameOfCollection);
            collection.insertOne(document);
        } catch (Exception ex) {
            throw new StoredException("Failed to save document to database in colletion: " + nameOfCollection, ex);
        }
    }

    public Document getDocumentbyId(String collection, String key, String value) {
        try {
            Document document = new Document();
            document.put(key, value);
            return getCollection(collection).find(document).first();
        } catch (Exception e) {
            throw new StoredException("Failed to get document by id: " + value + " from collection " + collection, e);
        }
    }

    protected <T> List<T> getCollectionToClass(String collection, Class<T> clazz) {
        try {
            List<T> list = new ArrayList<>();
            Gson json = JsonBuilder.build();
            getCollection(collection).find().forEach((Consumer<? super Document>) (Document document) -> {
                list.add(json.fromJson(document.toJson(), (java.lang.reflect.Type) clazz));
            });
            return list;
        } catch (Exception e) {
            throw new StoredException("Failed to get collection " + collection + " to class " + clazz.getName(), e);
        }
    }

//    public <T> List<T> getCollectionValuesToClass(String collection, Class<T> clazz) {
//        try {
//            List<T> list = new ArrayList<>();
//            Gson json = JsonBuilder.build();
//            getCollection(collection).find().forEach((Consumer<? super Document>) (Document document) -> {
//                Document d = (Document) document.get("value");
//                list.add(json.fromJson(d.toJson(), (java.lang.reflect.Type) clazz));
//            });
//            return list;
//        } catch (Exception e) {
//            throw new StoredException("Failed to get collection " + collection + " to class " + clazz.getName(), e);
//        }
//    }

    protected <T> T getLastObjectToClass(String collection, Class<T> clazz) {
        try {
            Document document = getCollection(collection).find().sort(new Document("_id", -1)).first();
            Gson json = JsonBuilder.build();
            assert document != null;
            return json.fromJson(document.toJson(), (java.lang.reflect.Type) clazz);
        } catch (Exception e) {
            throw new StoredException("Failed to get collection " + collection + " to class " + clazz.getName(), e);
        }
    }

    protected <T> List<T> findMany(String key, String value, String collectionName, Class<T> clazz) {
        try {
            Gson json = JsonBuilder.build();
            List<T> list = new ArrayList<>();
            getCollection(collectionName).find(new Document(key, value))
                    .forEach((Consumer<? super Document>) (Document document) -> {
                        list.add(json.fromJson(document.toJson(), (java.lang.reflect.Type) clazz));
                    });
            return list;
        } catch (Exception e) {
            throw new StoredException("Failed to get collection " + collectionName + " to class " + clazz.getName(), e);
        }
    }

    protected <T> T findOne(String key, String value, String collectionName, Class<T> clazz) {
        try {
            List<T> list = findMany(key, value, collectionName, clazz);
            if (list.size() != 1) {
                throw new Exception("Not exacly one element of key: " + key + " and value " + value + " in collection "
                        + collectionName);
            }
            return list.get(0);
        } catch (Exception e) {
            throw new StoredException("Failed to get collection " + collectionName + " to class " + clazz.getName(), e);
        }
    }

    public void updateEntry(String id, Object entry, String collectionName) {
        updateEntry(collectionName, id, entry, "_id");
    }

    public void updateEntry(String id, Object entry) {
        updateEntry("mix", id, entry, "_id");
    }

    public void updateEntry(String collectionName, String id, Object entry, String key) {
        try {
            assertEntryExists(id, collectionName);
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            Document document = getDocumentbyId(collectionName, key, id);
            String json = Utils.toJson(new Wrapper(new RawJson(entry)));
            Document document1 = Document.parse(json);
            document.put(key, id);
            collection.findOneAndReplace(document, document1);
        } catch (Exception e) {
            throw new StoredException("Failed to update entry for id: " + id, e);
        }
    }

    public <T> void addEntry(String id, T entry, String collection) {
        try {
            String json = Utils.toJson(new Wrapper(new RawJson(entry)));
            Document document = Document.parse(json);
            document.put("_id", id);
            saveToDataBase(document, collection);
        } catch (Exception ex) {
            throw new StoredException("Failed to update entry for id: " + id, ex);
        }
    }

    public <T> T getOrAddEntry(String id, T defaultValue, Class<T> entryType, String collection) {
        if (!isEntryExists(id)) {
            addEntry(id, defaultValue, collection);
            return defaultValue;
        } else {
            return getEntry(id, entryType);
        }
    }

    public MongoCollection<?> getMongoCollection(String name) {
        return mongoDatabase.getCollection(name);
    }

    @Override
    public void close() {
        mongoClient.close();
    }

}
