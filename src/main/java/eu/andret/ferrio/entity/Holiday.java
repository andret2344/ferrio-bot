package eu.andret.ferrio.entity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Holiday(
		@NotNull String id, int day, int month, @NotNull String name, boolean usual,
		@NotNull String description, @NotNull List<String> categories) {
}
