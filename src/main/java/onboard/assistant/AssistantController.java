package onboard.assistant;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsListRequest;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.model.Attachment;
import com.github.seratch.jslack.api.model.Channel;
import com.github.seratch.jslack.shortcut.model.ApiToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.reactivestreams.client.MongoClient;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static io.micronaut.http.HttpStatus.OK;

@Controller
public class AssistantController {

    private static final Logger LOG = Logger.getLogger("Assistant Controller Logger");

    MongoClient mongoClient;
    AssistantService assistantService;
    JsonParser parser = new JsonParser();

    public AssistantController(MongoClient mongoClient, AssistantService assistantService) {
        this.mongoClient = mongoClient;
        this.assistantService = assistantService;
    }

    @Post("/assistant")
    public HttpResponse index(@Body String body) {
        JsonObject json = parser.parse(body).getAsJsonObject();

        if (json.get("challenge") != null) {
            String challenge = json.get("challenge").getAsString();
            String jsonResponse = new Gson().toJson("challenge: " + challenge);
            return HttpResponse.status(OK).body(jsonResponse);
        }

        IncomingMessage message = new Gson().fromJson(body, IncomingMessage.class);
        ApiToken token = ApiToken.of(null); //TODO: add token here but refactor into .yml file

        if (message.event.type.equals("member_joined_channel")) {
            Slack slack = Slack.getInstance();

            try {

                LOG.info("New user joined, attempting to respond");
                ChannelsListResponse channelsResponse = slack.methods().channelsList(
                        ChannelsListRequest.builder().token(token.getValue()).build());

                Channel channel = channelsResponse.getChannels().stream()
                        .filter(c -> c.getId().equals(message.event.channel)).findFirst().get();

                List attachments = assistantService.getAttachments();

                String introduction = "Hi <@" + message.event.user + "> I'm the onboarding assistant for CGI,\n " +
                        "I'm here to make the process easier for you\n" +
                        "You have lots to do over the next few days\n" +
                        "Shall we get started";

                slack.methods().chatPostMessage(
                        ChatPostMessageRequest.builder()
                                .token(token.getValue())
                                .channel(channel.getId())
                                .text(introduction)
                                .mrkdwn(true)
                                .attachments(attachments)
                                .build());
                LOG.info("Response message sent to the user at channel: " + channel.getName());
                //TODO: after removing the above section of code tie the post message to the general channel
                // TODO: within slack itself and
                //TODO: remove the code comments
            } catch (SlackApiException | IOException ex) {
                LOG.info("An exception occurred: " + ex.getMessage());
            }
        }
        return HttpResponse.status(OK);
    }

    @Post("/assistant/attachment")
    public void createAttachment(@Body Attachment attachment) {
        assistantService.storeAttachment(attachment);
    }
}