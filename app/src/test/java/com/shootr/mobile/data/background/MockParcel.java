package com.shootr.mobile.data.background;

import android.os.Parcel;
import java.util.ArrayList;
import java.util.List;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockParcel {

    public static Parcel obtain() {
        return new MockParcel().getMockedParcel();
    }

    Parcel mockedParcel;
    int position;
    List<Object> objects;

    public Parcel getMockedParcel() {
        return mockedParcel;
    }

    public MockParcel() {
        mockedParcel = mock(Parcel.class);
        objects = new ArrayList<>();
        setupMock();
    }

    private void setupMock() {
        setupWrites();
        setupReads();
        setupOthers();
    }

    private void setupWrites() {
        Answer<Void> writeValueAnswer = new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                Object parameter = invocation.getArguments()[0];
                objects.add(parameter);
                return null;
            }
        };
        doAnswer(writeValueAnswer).when(mockedParcel).writeLong(anyLong());
        doAnswer(writeValueAnswer).when(mockedParcel).writeString(anyString());
    }

    private void setupReads() {
        when(mockedParcel.readLong()).thenAnswer(new Answer<Long>() {
            @Override public Long answer(InvocationOnMock invocation) throws Throwable {
                return (Long) objects.get(position++);
            }
        });
        when(mockedParcel.readString()).thenAnswer(new Answer<String>() {
            @Override public String answer(InvocationOnMock invocation) throws Throwable {
                return (String) objects.get(position++);
            }
        });
    }

    private void setupOthers() {
        doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                position = ((Integer) invocation.getArguments()[0]);
                return null;
            }
        }).when(mockedParcel).setDataPosition(anyInt());
    }
}
