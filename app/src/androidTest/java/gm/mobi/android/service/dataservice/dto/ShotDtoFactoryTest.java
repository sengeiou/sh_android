package gm.mobi.android.service.dataservice.dto;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ShotDtoFactoryTest {


    ShotDtoFactory shotDtoFactory;
    UtilityDtoFactory utilityDtoFactory;
    ShotMapper shotMapper;

    @Before
    public void setup() {
        utilityDtoFactory = mock(UtilityDtoFactory.class);
        shotMapper = mock(ShotMapper.class);
        shotDtoFactory = new ShotDtoFactory(utilityDtoFactory, shotMapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newShotFailsWhenIdUserNull() {
        shotDtoFactory.getNewShotOperationDto(null, "");
    }
    @Test(expected = IllegalArgumentException.class)
    public void newShotFailsWhenIdUserZero() {
        shotDtoFactory.getNewShotOperationDto(0L, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void newShotFailsWhenIdUserNegative() {
        shotDtoFactory.getNewShotOperationDto(-5L, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void newShotFailsWhenCommentNull() {
        shotDtoFactory.getNewShotOperationDto(5L, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newShotFailsWhenCommentEmpty() {
        shotDtoFactory.getNewShotOperationDto(5L, "    \n");
    }

    @Test
    public void operationDtoIsConstructedWithDataFromShotMapper() {
        String comment = "comment";
        Map<String, Object> mockedData = new HashMap<>();
        mockedData.put(GMContract.ShotTable.COMMENT, comment);
        when(shotMapper.toDto(any(Shot.class))).thenReturn(mockedData);

        GenericDto genericDto = new GenericDto();
        ArgumentCaptor<OperationDto> operationDtoArgumentCaptor = ArgumentCaptor.forClass(OperationDto.class);
        when(
          utilityDtoFactory.getGenericDtoFromOperation(anyString(), operationDtoArgumentCaptor.capture())).thenReturn(
          genericDto);

        shotDtoFactory.getNewShotOperationDto(5L, comment);

        OperationDto operationDto = operationDtoArgumentCaptor.getValue();
        Map<String, Object> buildedData = operationDto.getData()[0];
        System.out.println(buildedData);
        assertThat(buildedData).isNotNull();
        assertThat(buildedData).containsEntry(GMContract.ShotTable.COMMENT, comment);
    }
}
