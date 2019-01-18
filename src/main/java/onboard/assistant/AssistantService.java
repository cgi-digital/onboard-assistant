package onboard.assistant;

import com.github.seratch.jslack.api.model.Attachment;

import java.util.List;

public interface AssistantService {

    List<Attachment> getAttachments();
    void storeAttachment(Attachment attachment);

}
