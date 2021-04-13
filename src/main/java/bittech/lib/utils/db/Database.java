package bittech.lib.utils.db;

import bittech.lib.utils.Config;
import bittech.lib.utils.Require;
import bittech.lib.utils.Utils;
import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.json.JsonBuilder;
import bittech.lib.utils.json.RawJson;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Database implements AutoCloseable {

    private final AtomicBoolean enableWriteAccessCheck;
    private final String uuid;

    private static final String MIX_COLLECTION_NAME = "mix";

    private final MongoDatabase mongoDatabase;
    private final MongoClient mongoClient;

    private Runnable evWriteAccessLost;

    private AtomicBoolean opened;

    public Database(String uriStr, String dbName) {
        try {
            enableWriteAccessCheck = new AtomicBoolean(false);
            opened = new AtomicBoolean(false);
            System.out.println("Connecting to database " + dbName + ": " + uriStr + "... ");
            this.uuid = UUID.randomUUID().toString();
            MongoClientURI uri = new MongoClientURI(uriStr);
            CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries
                            .fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            mongoClient = new MongoClient(uri);
            mongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(pojoCodecRegistry);
            if (!collectionExists(MIX_COLLECTION_NAME)) {
                createCollection(MIX_COLLECTION_NAME);
            }
            enableWriteAccessCheck.set(true);
            opened.set(true);
            System.out.println("Database has been initialized ! ");
        } catch (Exception ex) {
            throw new StoredException("Failed to init db", ex);
        }
    }

    public Database() {
        this(Config.getInstance().getEntry("mongodbUri", String.class),
                Config.getInstance().getEntry("mongodbName", String.class));
    }

    public void onWriteAccessLost(Runnable evWriteAccessLost) {
        this.evWriteAccessLost = Require.notNull(evWriteAccessLost, "evWriteAccessLost");
    }

    public void disableWriteAccessCheck() {
        enableWriteAccessCheck.set(false);
    }

    public void dropIndexes(String collection) {
        mongoDatabase.getCollection(collection).dropIndexes();
    }

    public void deleteManyDocuments(String collection, BasicDBObject bson) {
        mongoDatabase.getCollection(collection).deleteMany(bson);
    }

    public void applyWriteAccess() {
        enableWriteAccessCheck.set(false);
        addOrUpdate(MIX_COLLECTION_NAME, "writeAccess", uuid);
        enableWriteAccessCheck.set(true);
    }

    public void assertWriteAccess() {
        try {
            if (!enableWriteAccessCheck.get()) {
                return;
            }
            if (!collectionExists("mix")) {
                throw new Exception("No mix collection = no write acces entry");
            }
            String accessUuid = this.getEntry("writeAccess", String.class);
            if (!accessUuid.equals(uuid)) {
                throw new Exception("DB write access was assigned by someone else. Our UUID= " + uuid + " but there is " + accessUuid);
            }
        } catch (Exception ex) {
            if (evWriteAccessLost != null) {
                evWriteAccessLost.run();
            }
            throw new StoredException("No rights to write to DB", ex);
        }
    }

    public boolean dbInit() { // TODO: Ogolna wartosc
        return getOrAddEntry("isInitializedTokensList", false, Boolean.class, "mix");
    }

    private void maybeCreateIndex(com.mongodb.client.MongoCollection<org.bson.Document> collection, String key) {
        collection.createIndex(Indexes.ascending(key));
    }

    public void deleteCollection(String collection) {
        assertWriteAccess();
        mongoDatabase.getCollection(collection).drop();
    }


    public static void connectAndClean(String uri, String name) {
        try (Database database = new Database(uri, name)) {
            database.applyWriteAccess();
            database.clean();
        }
    }

    public void clean() {
        try {
            assertWriteAccess();
            for (String name : mongoDatabase.listCollectionNames()) {
                mongoDatabase.getCollection(name).drop();
            }
        } catch (Exception e) {
            throw new StoredException("Failed to delete all data from database ", e);
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
            if (document1 == null) {
                throw new Exception("No entry in mix collection for _id = " + id);
            }
            return docToObj(document1, clazz);
        } catch (Exception ex) {
            throw new StoredException("Failed to get entry for id " + id, ex);
        }
    }

    private static <T> T docToObj(Document document, Class<T> clazz) {
        String strJson = document.toJson();
        Wrapper myJson = JsonBuilder.build().fromJson(strJson, Wrapper.class);
        return myJson.value.fromJson(clazz);
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
                assertWriteAccess();
                mongoDatabase.createCollection(nameOfCollection);
            }
        } catch (Exception e) {
            throw new StoredException("failed to create collection: " + nameOfCollection, e);
        }
    }

    protected void deleteDocument(String key, String value, String nameOfCollection) {
        try {
            assertWriteAccess();
            mongoDatabase.getCollection(nameOfCollection).deleteOne(new Document(key, value));
        } catch (Exception e) {
            throw new StoredException(
                    "Failed to delete document from " + nameOfCollection + " key " + key + " value " + value, e);
        }
    }

    protected void deleteManyDocument(String key, String value, String nameOfCollection) {
        try {
            assertWriteAccess();
            mongoDatabase.getCollection(nameOfCollection).deleteMany(new Document(key, value));
        } catch (Exception e) {
            throw new StoredException(
                    "Failed to delete document from " + nameOfCollection + " key " + key + " value " + value, e);
        }
    }

    public boolean existObject(String key, String value, String nameOfCollection) {
        try {
            long size = mongoDatabase.getCollection(nameOfCollection).countDocuments(new Document(key, value));
            return size > 0;
        } catch (Exception e) {
            return false;
        }
    }


//    public boolean existObject(String key, String value, String nameOfCollection) {
//        try {
//
//
//
//            MongoCollection<Document> collectionToDeleteDocument = mongoDatabase.getCollection(nameOfCollection);
//            for (Document document : collectionToDeleteDocument.find()) {
//                if (document.get(key).equals(value)) {
//                    return true;
//                }
//            }
//            return false;
//        } catch (Exception ex) {
//            throw new StoredException("Failed to check if object exist for key: " + key + " and value " + value
//                    + "in collection " + nameOfCollection, ex);
//        }
//    }

    public boolean isEntryExists(String id) {
        return existObject("_id", id, MIX_COLLECTION_NAME);
    }

    protected void assertEntryExists(String id, String collectionName) {
        if (!existObject("_id", id, collectionName)) {
            throw new StoredException(
                    "Such entry do not exist with id: " + id + " in collection " + MIX_COLLECTION_NAME, null);
        }
    }


    protected void assertCollectionExists(String nameOfCollection) throws Exception {
        if (!collectionExists(nameOfCollection)) {
            throw new Exception("Such collection do not exists: " + nameOfCollection);
        }
    }

    public void saveToDataBase(Document document, String nameOfCollection) {
        try {
            assertWriteAccess();
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

    public <T> List<T> findMany(String key, String value, String collectionName, Class<T> clazz) {
        try {
            Gson json = JsonBuilder.build();
            List<T> list = new ArrayList<>();
            MongoCollection<Document> collection = getCollection(collectionName);
            maybeCreateIndex(collection, key);
            getCollection(collectionName).find(new Document(key, value))
                    .forEach((Consumer<? super Document>) (Document document) -> {
                        list.add(json.fromJson(document.toJson(), (java.lang.reflect.Type) clazz));
                    });

//            Wersja docelowa:
//            FindIterable<Document> docs = collection.find(new Document(key, value));
//
//            docs.forEach((Consumer<? super Document>) (Document document) -> {
//                list.add(docToObj(document, clazz));

            return list;
        } catch (Exception e) {
            throw new StoredException("Failed to get collection " + collectionName + " to class " + clazz.getName(), e);
        }
    }

    public <T> T findOne(String key, String value, String collectionName, Class<T> clazz) {
        try {
            List<T> list = findMany(key, value, collectionName, clazz);
            if (list.size() == 0) {
                throw new Exception("No even single element of key: " + key + " and value " + value + " in collection "
                        + collectionName);
            }
            if (list.size() > 1) {
                throw new Exception("More than one element of key: " + key + " and value " + value + " in collection "
                        + collectionName);
            }
            return list.get(0);
        } catch (Exception e) {
            throw new StoredException("Failed to get collection " + collectionName + " to class " + clazz.getName(), e);
        }
    }


    public void updateEntry(String id, Object entry) {
        assertWriteAccess();
        updateEntry("mix", id, entry, "_id");
    }

    public void updateEntry(String collectionName, String id, Object entry, String key) {
        try {
            assertWriteAccess();
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

    public void addOrUpdate(String collectionName, String id, Object entry) {
        assertWriteAccess();
        if (isEntryExists(id)) {
            updateEntry(collectionName, id, entry, "_id");
        } else {
            addEntry(id, entry, collectionName);
        }
    }

    public <T> void addEntry(String id, T entry, String collection) {
        try {
            assertWriteAccess();
            String json = Utils.toJson(new Wrapper(new RawJson(entry)));
            Document document = Document.parse(json);
            document.put("_id", id);
            saveToDataBase(document, collection);
        } catch (Exception ex) {
            throw new StoredException("Failed to update entry for id: " + id, ex);
        }
    }

    public <T> T getOrAddEntry(String id, T defaultValue, Class<T> entryType, String collection) {
        assertWriteAccess();
        if (!isEntryExists(id)) {
            addEntry(id, defaultValue, collection);
            return defaultValue;
        } else {
            return getEntry(id, entryType);
        }
    }

    public boolean isOpened() {
        return opened.get();
    }

    @Override
    public void close() {
        mongoClient.close();
        opened.set(false);
    }

}