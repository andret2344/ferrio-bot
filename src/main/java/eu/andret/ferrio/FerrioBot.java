package eu.andret.ferrio;

import eu.andret.ferrio.command.FerrioCommand;
import eu.andret.ferrio.util.Requestor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public final class FerrioBot {
	private static final String TOKEN_VARIABLE = "FERRIO_TOKEN";
	private static final String LOG_LEVEL_VARIABLE = "FERRIO_LOG_LEVEL";
	private static final Logger LOGGER = LoggerFactory.getLogger(FerrioBot.class);

	public static void main(final String[] args) {
		Configurator.setRootLevel(Level.toLevel(System.getenv(LOG_LEVEL_VARIABLE), Level.INFO));

		final String token = System.getenv(TOKEN_VARIABLE);
		if (token == null || token.isBlank()) {
			LOGGER.error("Missing the {} environment variable", TOKEN_VARIABLE);
			System.exit(1);
		}

		final JDA jda = JDABuilder.createLight(token, Collections.emptyList())
				.setStatus(OnlineStatus.DO_NOT_DISTURB)
				.addEventListeners(new FerrioCommand(new Requestor()))
				.build();

		jda.updateCommands()
				.addCommands(
						Commands
								.slash("ferrio", "Get random today holiday")
								.addOptions(new OptionData(OptionType.STRING, "language", "Language of the holiday", false)
										.addChoice("Polish", "pl")
										.addChoice("English", "en"))
								.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VIEW_CHANNEL)))
				.queue();
	}
}
