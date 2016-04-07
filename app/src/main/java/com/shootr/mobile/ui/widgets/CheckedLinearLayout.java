package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * LinearLayout que implementa la interfaz Checkable para
 * gestionar estados de selección/deselección
 */
public class CheckedLinearLayout extends LinearLayout implements Checkable {

    /**
     * Esta variable es la que nos sirve para almacenar el estado de este widget
     */
    private boolean mChecked=false;

    private List<Checkable> checkableViews = new ArrayList<>();

    /**
     * Este array se usa para que los drawables que se usen
     * reaccionen al cambio de estado especificado
     * En nuestro caso al "state_checked"
     * que es el que utilizamos en nuestro selector
     */
    private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

    public CheckedLinearLayout(Context context) {
        super(context);
    }

    public CheckedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = this.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            findCheckableChildren(this.getChildAt(i));
        }
    }

    /**
     * Add to our checkable list all the children of the view that implement the
     * interface Checkable
     */
    private void findCheckableChildren(View v) {
        if (v instanceof Checkable) {
            this.checkableViews.add((Checkable) v);
        }
        if (v instanceof ViewGroup) {
            final ViewGroup vg = (ViewGroup) v;
            final int childCount = vg.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                findCheckableChildren(vg.getChildAt(i));
            }
        }
    }

    /**
     * Este method es el que cambia el estado de nuestro widget
     * @param checked true para activarlo y false para desactivarlo
     */
    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        //Cuando cambiamos el estado, debemos informar a los drawables
        //que este widget tenga vinculados
        refreshDrawableState();
        for (Checkable c : checkableViews) {
            // Pass the information to all the child Checkable widgets
            c.setChecked(checked);
        }
        invalidate();
    }

    /**
     * Este method devuelve el estado de nuestro widget <img src="http://androcode.es/wp-includes/images/smilies/icon_smile.gif" alt=":)" class="wp-smiley">
     * @return true o false, no?
     */
    @Override
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * Este method cambia el estado de nuestro widget
     * Si estaba activo se desactiva y viceversa
     */
    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    /**
     * Este method es un poco más complejo
     * Se encarga de combinar los diferentes "estados" de un widget
     * para informar a los drawables.
     *
     * Si nuestro widget está "checked" le añadimos el estado CHECKED_STATE_SET
     * que definimos al principio
     *
     * @return el array de estados de nuestro widget
     */
    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
}