package eu.andret.ferrio.command;

import com.google.gson.reflect.TypeToken;
import eu.andret.ferrio.entity.Holiday;
import eu.andret.ferrio.util.Requestor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class FerrioCommand extends ListenerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(FerrioCommand.class);
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String TAG_SEPARATOR = " · ";
	private static final String FERRIO_URL = "https://ferrio.app";
	private static final String FERRIO_ICON_URL = "https://ferrio.app/images/Ferrio%20square.png";
	private static final TypeToken<List<Holiday>> HOLIDAY_LIST_TYPE = new TypeToken<>() {
	};

	private final Requestor requestor;

	public FerrioCommand(@NotNull final Requestor requestor) {
		this.requestor = requestor;
	}

	@Override
	public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
		LOGGER.debug("Slash command: FerrioCommand");
		if (!event.getName().equals("ferrio")) {
			return;
		}
		final String language = event.getOption("language", "pl", OptionMapping::getAsString);
		LOGGER.info("Executed command: /ferrio [language={}]", language);
		event.deferReply().queue();
		final LocalDate now = LocalDate.now(ZoneId.systemDefault());
		final String url = String.format("https://api.ferrio.app/v3/holidays?lang=%s&day=%d&month=%d", language, now.getDayOfMonth(), now.getMonthValue());
		LOGGER.debug("Requesting URL: {}", url);
		requestor.executeRequest(url, HOLIDAY_LIST_TYPE)
				.exceptionally(throwable -> {
					LOGGER.error("Failed to fetch holidays from {}", url, throwable);
					event.getHook().editOriginal("Sorry, I couldn't reach the Ferrio API. Please try again later.").queue();
					return null;
				})
				.thenAccept(holidays -> {
					if (holidays == null) {
						return;
					}
					LOGGER.debug("Response: {}", holidays);
					if (holidays.isEmpty()) {
						event.getHook().editOriginal("No holidays found for today.").queue();
						return;
					}
					try {
						final Holiday holiday = holidays.get(RANDOM.nextInt(holidays.size()));
						final EmbedBuilder embed = new EmbedBuilder()
								.setAuthor("FerrioBot", FERRIO_URL)
								.setTitle(holiday.name())
								.setDescription(holiday.description())
								.setThumbnail(FERRIO_ICON_URL)
								.addField("", "Powered by [ferrio.app](" + FERRIO_URL + ")", false)
								.setTimestamp(ZonedDateTime.now(ZoneId.systemDefault()));
						if (!holiday.categories().isEmpty()) {
							embed.addField("", String.join(TAG_SEPARATOR, holiday.categories()), false);
						}
						event.getHook()
								.editOriginal("")
								.setEmbeds(embed.build())
								.queue();
					} catch (final Exception e) {
						LOGGER.error("Failed while building embed", e);
						event.getHook().editOriginal("Sorry, an error occurred while preparing the response.").queue();
					}
				});
	}
}
