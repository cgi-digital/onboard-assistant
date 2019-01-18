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

import static io.micronaut.http.HttpStatus.OK;

@Controller
public class AssistantController {

    MongoClient mongoClient;
    AssistantService assistantService;
    JsonParser parser = new JsonParser();

    public AssistantController(MongoClient mongoClient, AssistantService assistantService) {
        this.mongoClient = mongoClient;
        this.assistantService = assistantService;
    }


    @Post("/assistant")
    public HttpResponse index(@Body String body) throws IOException, SlackApiException {
        JsonObject json = parser.parse(body).getAsJsonObject();

        if (json.get("challenge") != null) {
            String challenge = json.get("challenge").getAsString();
            String jsonResponse = new Gson().toJson("challenge: " + challenge);
            return HttpResponse.status(OK).body(jsonResponse);
        }

        IncomingMessage message = new Gson().fromJson(body, IncomingMessage.class);
        ApiToken token = ApiToken.of("xoxb-379873439939-527270329015-mIc7AHUuS7DUffWwoRbJHuqf");

        if (message.event.type.equals("member_joined_channel")) {
            Slack slack = Slack.getInstance();

            ChannelsListResponse channelsResponse = slack.methods().channelsList(
                    ChannelsListRequest.builder().token(token.getValue()).build());

            Channel softwareEngineering = channelsResponse.getChannels().stream()
                    .filter(c -> c.getName().equals("software-engineering")).findFirst().get();

            List attachments = assistantService.getAttachments();

            //TODO: Remove this section of code after development

            Channel channel = channelsResponse.getChannels().stream()
                    .filter(c -> c.getId().equals(message.event.channel)).findFirst().get();

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


            //TODO: after removing the above section of code tie the post message to the general channel and
            //TODO: remove the code comments

        }
        return HttpResponse.status(OK);
    }

    @Post("/assistant/attachment")
    public void createAttachment(@Body Attachment attachment) {
        assistantService.storeAttachment(attachment);
    }
}