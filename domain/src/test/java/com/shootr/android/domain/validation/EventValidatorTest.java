package com.shootr.android.domain.validation;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventValidatorTest {


    @Before
    public void setUp() throws Exception {
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

    private String filter(String original) {
        return original.replaceAll(EventValidator.EMOJI_RANGE_REGEX, "");
    }
}