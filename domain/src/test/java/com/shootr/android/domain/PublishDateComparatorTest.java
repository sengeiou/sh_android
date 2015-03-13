package com.shootr.android.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PublishDateComparatorTest {

    private static final Long DATE_OLDER = 1L;
    private static final Long DATE_MIDDLE = 2L;
    private static final Long DATE_NEWER = 3L;

    @Test
    public void shouldNotBeEqualTwoShotsWithDifferentDates() throws Exception {
        Shot shot1 = new Shot();
        shot1.setPublishDate(new Date(DATE_OLDER));
        Shot shot2 = new Shot();
        shot2.setPublishDate(new Date(DATE_NEWER));

        assertThat(shot1).isNotEqualTo(shot2);
    }

    @Test
    public void shouldOrderListInDescendingDateOrder() throws Exception {
        List<Shot> unorderedShotList = unorderedShots();

        Collections.sort(unorderedShotList, new Shot.PublishDateComparator());

        assertThat(unorderedShotList).containsSequence(orderedShotSequence());
    }

    private Shot[] orderedShotSequence() {
        return new Shot[]{shotWithDate(DATE_NEWER), shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER),};
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