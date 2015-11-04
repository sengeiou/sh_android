package com.shootr.mobile.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NewerBelowComparatorTest {

    private static final Long DATE_OLDER = 1000L;
    private static final Long DATE_MIDDLE = 2000L;
    private static final Long DATE_NEWER = 3000L;

    @Test
    public void shouldNotBeEqualTwoShotsWithDifferentDates() throws Exception {
        Shot shot1 = new Shot();
        shot1.setPublishDate(new Date(DATE_OLDER));
        Shot shot2 = new Shot();
        shot2.setPublishDate(new Date(DATE_NEWER));

        assertThat(shot1).isNotEqualTo(shot2);
    }

    @Test
    public void shouldOrderListInAscendingDateOrder() throws Exception {
        List<Shot> unorderedShotList = unorderedShots();

        Collections.sort(unorderedShotList, new Shot.NewerBelowComparator());

        assertThat(unorderedShotList).containsSequence(orderedShotSequenceWithNewerLast());
    }

    private Shot[] orderedShotSequenceWithNewerLast() {
        return new Shot[]{shotWithDate(DATE_OLDER), shotWithDate(DATE_MIDDLE), shotWithDate(DATE_NEWER),};
    }

    private List<Shot> unorderedShots() {
        return Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER), shotWithDate(DATE_NEWER));
    }

    private Shot shotWithDate(Long date) {
        Shot shot = new Shot();
        shot.setPublishDate(new Date(date));
        return shot;
    }
}