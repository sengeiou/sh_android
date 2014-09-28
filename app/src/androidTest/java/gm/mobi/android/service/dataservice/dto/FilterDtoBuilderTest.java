package gm.mobi.android.service.dataservice.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import gm.mobi.android.db.GMContract.ShotTable;
import gm.mobi.android.service.dataservice.generic.FilterDto;

import static gm.mobi.android.service.dataservice.generic.FilterBuilder.and;
import static gm.mobi.android.service.dataservice.generic.FilterBuilder.orModifiedOrDeletedAfter;
import static gm.mobi.android.service.dataservice.generic.FilterBuilder.or;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class FilterDtoBuilderTest {


}
