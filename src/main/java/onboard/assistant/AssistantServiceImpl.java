package onboard.assistant;

import com.github.seratch.jslack.api.model.Attachment;
import com.google.gson.Gson;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.bson.Document;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AssistantServiceImpl implements AssistantService {

    private MongoClient mongoClient;

    public AssistantServiceImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public List<Attachment> getAttachments() {
        List<Attachment> attachments = new ArrayList<>();
        Single<List<Document>> attachmentsSingle = Flowable.fromPublisher(getCollection().find()).toList();
        attachmentsSingle.blockingGet().forEach(document -> {
            Attachment attachment = new Gson().fromJson(document.toJson(), Attachment.class);
            attachments.add(attachment);
        });
        return attachments;
    }

    @Override
    public void storeAttachment(Attachment attachment) {
        Single.fromPublisher(getCollection().insertOne(null)).subscribe();
    }

    private MongoCollection<Document> getCollection() {
        return this.mongoClient.getDatabase("onboarding").getCollection("attachments");
    }
}
