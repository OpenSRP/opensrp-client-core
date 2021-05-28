package org.smartregister.util;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.R;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 29-09-2020.
 */
public class DatePickerUtilsTest extends BaseRobolectricUnitTest {

    @Test
    public void preventShowingKeyboard() {
        DatePicker datePicker = Mockito.mock(DatePicker.class);
        DatePickerUtils.preventShowingKeyboard(datePicker);

        Mockito.verify(datePicker).setDescendantFocusability(Mockito.eq(ViewGroup.FOCUS_BLOCK_DESCENDANTS));
    }

    @Test
    public void testThemeDatePickerShouldAddNumberPickersInTheRightOrder() {
        View view = LayoutInflater.from(RuntimeEnvironment.application)
                .inflate(R.layout.test_html, null);
        DatePicker datePicker = view.findViewById(R.id.test_date_picker);

        int idYear = Resources.getSystem().getIdentifier("year", "id", "android");
        int idMonth = Resources.getSystem().getIdentifier("month", "id", "android");
        int idDay = Resources.getSystem().getIdentifier("day", "id", "android");

        int idLayout = Resources.getSystem().getIdentifier("pickers", "id", "android");
        LinearLayout layout = datePicker.findViewById(idLayout);

        // Call the method under test
        DatePickerUtils.themeDatePicker(datePicker, new char[]{'d', 'y', 'm'});

        // Assert the properties
        Assert.assertEquals(3, layout.getChildCount());

        int idPickerInput = Resources.getSystem().getIdentifier("numberpicker_input", "id", "android");
        TextView dayDateTv = ((NumberPicker) layout.getChildAt(0)).findViewById(idPickerInput);
        Assert.assertEquals(EditorInfo.IME_ACTION_NEXT, dayDateTv.getImeOptions());

        TextView monthDateTv = ((NumberPicker) layout.getChildAt(1)).findViewById(idPickerInput);
        Assert.assertEquals(EditorInfo.IME_ACTION_NEXT, monthDateTv.getImeOptions());

        TextView yearDateTv = ((NumberPicker) layout.getChildAt(2)).findViewById(idPickerInput);
        Assert.assertEquals(EditorInfo.IME_ACTION_DONE, yearDateTv.getImeOptions());

        // Assert the order of views
        Assert.assertEquals(idDay, layout.getChildAt(0).getId());
        Assert.assertEquals(idYear, layout.getChildAt(1).getId());
        Assert.assertEquals(idMonth, layout.getChildAt(2).getId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testThemeDatePickerShouldThrowExceptionWhenInValidYmDOrderIsProvided() {
        View view = LayoutInflater.from(RuntimeEnvironment.application)
                .inflate(R.layout.test_html, null);
        DatePicker datePicker = Mockito.spy(view.findViewById(R.id.test_date_picker));

        // Call the method under test
        DatePickerUtils.themeDatePicker(datePicker, new char[]{'e', 'y', 'm'});
    }

}