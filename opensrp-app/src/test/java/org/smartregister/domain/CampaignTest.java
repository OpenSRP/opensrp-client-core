package org.smartregister.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;

import static org.junit.Assert.assertEquals;
import static org.smartregister.domain.Task.TaskStatus.IN_PROGRESS;

/**
 * Created by samuelgithengi on 11/22/18.
 */
public class CampaignTest {

	private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
			.registerTypeAdapter(LocalDate.class, new DateTypeConverter())
			.create();

	private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HHmm");

	private String campaignJson = "{\"identifier\":\"IRS_2018_S1\",\"title\":\"2019 IRS Season 1\",\"description\":\"This is the 2010 IRS Spray Campaign for Zambia for the first spray season dated 1 Jan 2019 - 31 Mar 2019.\",\"status\":\"In Progress\",\"executionPeriod\":{\"start\":\"2019-01-01T0000\",\"end\":\"2019-03-31T0000\"},\"authoredOn\":\"2018-10-01T0900\",\"lastModified\":\"2018-10-01T0900\",\"owner\":\"jdoe\",\"serverVersion\":0}";

	@Test
	public void testDeserialize() {
		Campaign campaign = gson.fromJson(campaignJson, Campaign.class);
		assertEquals("IRS_2018_S1", campaign.getIdentifier());
		assertEquals("2019 IRS Season 1", campaign.getTitle());
		assertEquals(
				"This is the 2010 IRS Spray Campaign for Zambia for the first spray season dated 1 Jan 2019 - 31 Mar 2019.",
				campaign.getDescription());
		assertEquals("2019 IRS Season 1", campaign.getTitle());
		assertEquals("2019-01-01", campaign.getExecutionPeriod().getStart().toString("yyyy-MM-dd"));
		assertEquals("2019-03-31", campaign.getExecutionPeriod().getEnd().toString("yyyy-MM-dd"));
		assertEquals(IN_PROGRESS, campaign.getStatus());
		assertEquals("2018-10-01T0900", campaign.getAuthoredOn().toString(formatter));
		assertEquals("2018-10-01T0900", campaign.getLastModified().toString(formatter));
		assertEquals("jdoe", campaign.getOwner());

	}

	@Test
	public void testSerialize() {
		Campaign campaign = gson.fromJson(campaignJson, Campaign.class);
		assertEquals(campaignJson, gson.toJson(campaign));
		assertEquals(campaign.getTitle(),"2019 IRS Season 1");
	}



}
