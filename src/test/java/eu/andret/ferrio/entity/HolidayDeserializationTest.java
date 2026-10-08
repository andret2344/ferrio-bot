package eu.andret.ferrio.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HolidayDeserializationTest {
	private static final TypeToken<List<Holiday>> HOLIDAY_LIST_TYPE = new TypeToken<>() {
	};
	private final Gson gson = new Gson();

	@Test
	void testDeserializeFlatV3Response() {
		final String json = """
				[
					{
						"id": "fixed-1",
						"day": 4,
						"month": 5,
						"name": "Star Wars Day",
						"usual": false,
						"description": "May the Fourth be with you.",
						"country": null,
						"url": "https://ferrio.app/star-wars",
						"mature_content": false,
						"categories": ["Pop culture", "Movies"]
					},
					{
						"id": "floating-7",
						"day": 26,
						"month": 11,
						"name": "Thanksgiving",
						"usual": true,
						"description": "American holiday.",
						"country": "US",
						"url": "",
						"mature_content": false,
						"categories": []
					}
				]
				""";

		final List<Holiday> holidays = gson.fromJson(json, HOLIDAY_LIST_TYPE);

		assertEquals(2, holidays.size());

		final Holiday first = holidays.getFirst();
		assertEquals("fixed-1", first.id());
		assertEquals(4, first.day());
		assertEquals(5, first.month());
		assertEquals("Star Wars Day", first.name());
		assertFalse(first.usual());
		assertEquals("May the Fourth be with you.", first.description());
		assertEquals(List.of("Pop culture", "Movies"), first.categories());

		final Holiday second = holidays.get(1);
		assertEquals("floating-7", second.id());
		assertTrue(second.categories().isEmpty());
	}

	@Test
	void testDeserializeEmptyResponse() {
		final List<Holiday> holidays = gson.fromJson("[]", HOLIDAY_LIST_TYPE);
		assertTrue(holidays.isEmpty());
	}

	@Test
	void testIgnoreUnknownJsonFields() {
		final String json = """
				[{
					"id": "fixed-9",
					"day": 1,
					"month": 1,
					"name": "X",
					"usual": true,
					"description": "Y",
					"categories": [],
					"some_new_field": "ignored",
					"another": 42
				}]
				""";

		final List<Holiday> holidays = gson.fromJson(json, HOLIDAY_LIST_TYPE);

		assertEquals(1, holidays.size());
		assertEquals("fixed-9", holidays.getFirst().id());
	}
}
