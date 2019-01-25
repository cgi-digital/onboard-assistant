package com.cgi.onboarding;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsListRequest;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import com.github.seratch.jslack.api.model.Attachment;
import com.github.seratch.jslack.shortcut.model.ApiToken;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.ArrayList;

import static org.springframework.http.HttpStatus.OK;

@SpringBootApplication
public class OnboardAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnboardAssistantApplication.class, args);
	}

	@Slf4j
	@Controller
	public static class AssistantController {

		public static final String CHALLENGE = "challenge";

		private String slackApiToken;
		private String mongoUri;
		private Gson gson;
		private JsonParser parser;
		private MongoClient mongoClient;

		public AssistantController(@Value("${spring.data.mongodb.uri}") String mongoUri,
								   @Value("${slack.auth.token}") String slackApiToken) {
			this.slackApiToken = slackApiToken;
			gson = new Gson();
			parser = new JsonParser();
			this.mongoUri = mongoUri;
			mongoClient = MongoClients.create(mongoUri);
		}

		@PostMapping("/assistant")
		public ResponseEntity index(@RequestBody String body) {

			val json = parser.parse(body).getAsJsonObject();
			if (json.get(CHALLENGE) != null) {
				log.info("Incoming challenge request responding with challenge");
				val challenge = json.get(CHALLENGE).getAsString();
				val jsonResponse = gson.toJson(CHALLENGE + ": " + challenge);
				return new ResponseEntity<>(jsonResponse, OK);
			}

			val message = gson.fromJson(body, IncomingMessage.class);
			val token = ApiToken.of(slackApiToken);

			if (message.event.type.equals("member_joined_channel")) {
				val slack = Slack.getInstance();

				try {

					log.info("New user joined, attempting to respond to{}", message.event.user);
					val channelsResponse = slack.methods().channelsList(
							ChannelsListRequest.builder().token(token.getValue()).build());

					//TODO: change the channel id get to point to general
					val channel = channelsResponse.getChannels().stream()
							.filter(c -> c.getId().equals(message.event.channel)).findFirst().get();

					log.info("Have Channel to respond to, getting all required attachments");
					val attachments = new ArrayList<Attachment>();
					getCollection()
							.find().forEach((Block<Document>) document -> {
						Attachment attachment = gson.fromJson(document.toJson(), Attachment.class);
						attachments.add(attachment);
					});

					log.info("Have a list of attachments sending the response");
					val introduction = "Hi <@" + message.event.user + "> I'm the onboarding assistant for CGI,\n " +
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
					log.info("Response message sent to the user at channel: {}", channel.getName());
					//TODO: Change the channel to point to general, this will activate
					//TODO: when a new user joins
				} catch (SlackApiException | IOException ex) {
					log.info("An exception occurred: {}", ex.getMessage());
				}
			}
			return new ResponseEntity(OK);
		}

		@PostMapping("/assistant/attachment")
		public ResponseEntity createAttachment(@RequestBody Attachment attachment) {
			log.info("Storing a new attachment object {}", attachment);
			val document = Document.parse(gson.toJson(attachment));
			getCollection().insertOne(document);
			return new ResponseEntity(OK);
		}

		private MongoCollection<Document> getCollection() {
			log.info("Get database and collection onboarding/attchments");
			return mongoClient.getDatabase("onboarding").getCollection("attachments");
		}

	}

}

