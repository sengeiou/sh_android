package com.shootr.android.domain.validation;

import com.shootr.android.domain.Event;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventValidatorTest {

    private final String dummyEventTitle = "TITLE";
    private static final long ONE_YEAR_AND_A_DAY_MILLIS = 366L * 24L * 60L * 60L * 1000L;
    private static final long ONE_MONTH_MILLIS = 30L * 24L * 60L * 60L * 1000L;
    private EventValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new EventValidator();
    }

    @Test
    public void testSpacesAreNotRemoved() {
        String spaceString = " ";
        String filteredString = filter(spaceString);
        assertThat(filteredString).isEqualTo(spaceString);
    }

    @Test
    public void testAlphanumericCharactersAreNotRemoved() {
        String alphanumeric = "qwertyuiopasdfghjklñzxcvbnm1234567890";
        String upperAlphanumeric = alphanumeric.toUpperCase();

        String filtered = filter(alphanumeric);
        String filteredUpper = filter(upperAlphanumeric);

        assertThat(filtered).isEqualTo(alphanumeric);
        assertThat(filteredUpper).isEqualTo(upperAlphanumeric);
    }

    @Test
    public void testPunctuationCharacteresNotRemoved() {
        String punctuation = "-.,:;!?¡¿@_*+/%$·\"&(){}[]^ç\\|";
        String filter = filter(punctuation);
        assertThat(filter).isEqualTo(punctuation);
    }

    @Test
    public void testEmojiRegexRemovesEmoticonRangeEmojis() throws Exception {
        String emojiString = "☺😊😀😁😂😃😄😅😆😇😈😉😯😐😑😕😠😬😡😢😴😮😣😤😥😦😧😨😩😰😟😱😲😳😵😶😷😞😒😍😛😜😝😋😗😙😘😚😎😭😌😖😔😪😏😓😫🙋🙌🙍🙅🙆🙇🙎🙏😺😼😸😹😻😽😿😾🙀🙈🙉🙊💩👶👦👧👨👩👴👵💏💑👪👫👬👭👤👥👮👷💁💂👯👰👸🎅👼👱👲👳💃💆💇💅👻👹👺👽👾👿💀💪👀👂👃👣👄👅💋❤💙💚💛💜💓💔💕💖💗💘💝💞💟👍👎👌✊✌✋👊☝👆👇👈👉👋👏👐";
        String filteredString = filter(emojiString);
        assertThat(filteredString).isEmpty();
    }

    //region Event Starting Date Validations
    @Test
    public void shouldGetNoErrorsIfStartDateIsLessThanAYear() throws Exception {
        Event event = createEventWithTitleAndCorrectStartAndEndDates();
        List<FieldValidationError> validationErrors = validator.validate(event);
        assertThat(validationErrors).isEmpty();
    }

    @Test
    public void shouldGetAnErrorIfStartDateIsMoreThanAYear() throws Exception {
        Event event = createEventWithTitleAndIncorrectStartAndEndDates();
        List<FieldValidationError> validationErrors = validator.validate(event);
        assertThat(validationErrors).isNotEmpty();
    }
    //endregion

    @Test
    public void dummy() {
    }

    private String filter(String original) {
        return original.replaceAll(EventValidator.EMOJI_RANGE_REGEX, "");
    }

    //region Event Creators
    private Event createEventWithTitleAndCorrectStartAndEndDates() {
        Date date = new Date();
        Event event = new Event();
        event.setTitle(dummyEventTitle);
        event.setStartDate(new Date(date.getTime() + ONE_MONTH_MILLIS));
        return event;
    }

    private Event createEventWithTitleAndIncorrectStartAndEndDates() {
        Date date = new Date();
        Event event = new Event();
        event.setTitle(dummyEventTitle);
        event.setStartDate(new Date(date.getTime() + ONE_YEAR_AND_A_DAY_MILLIS));
        return event;
    }
    //endregion
}