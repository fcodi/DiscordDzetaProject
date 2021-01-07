package io.github.fcodi.discord;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

import java.util.Arrays;

public class ExampleBot {

	public static void main(String[] args) {
		final boolean TOKEN_FROM_SYSTEM_PROPERTY =
				Boolean.parseBoolean(System.getProperty("TEST"));
		final String TOKEN = TOKEN_FROM_SYSTEM_PROPERTY
				? System.getProperty("DISCORD_BOT_TOKEN")
				: System.getenv("DISCORD_BOT_TOKEN");
		System.out.println(TOKEN);
		GatewayDiscordClient client = DiscordClientBuilder.create(TOKEN)
				.build()
				.login()
				.block();

		client.getEventDispatcher().on(ReadyEvent.class)
				.subscribe(event -> {
					User self = event.getSelf();
					System.out.printf("Logged in as %s#%s%nТокен получен из %s",
							self.getUsername(), self.getDiscriminator(),
							TOKEN_FROM_SYSTEM_PROPERTY ? "SystemProperty" : "Environment");
				});

		client.getEventDispatcher().on(MessageCreateEvent.class)
				.map(MessageCreateEvent::getMessage)
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.filter(message -> {

					final String[] ORDERS = {
							"Выполнить приказ 66!",
							"Execute order 66!"
					};
					return Arrays.asList(ORDERS).contains(message.getContent());
					//message.getContent().equalsIgnoreCase("Execute order 66!");
				})
				.flatMap(Message::getChannel)
				.flatMap(channel -> channel.createMessage("https://www.youtube.com/watch?v=VlZREJDeeEY"))
				.subscribe();

		client.onDisconnect().block();
	}

}
