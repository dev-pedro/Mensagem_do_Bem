package br.dev_pedrorjas.mensagemdobem;

/**
 * Created by dev_pedrorjas on 22/07/15.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

public class PreferenciaTempo extends DialogPreference {

    private int ultimaHora = 0;
    private int ultimoMinuto = 0;
    private TimePicker picker = null;

    public static int obterHora(String tempo) {
        String[] fragmentos = tempo.split(":");
        return Integer.parseInt(fragmentos[0]);
    }

    public static int obterMinuto(String tempo) {
        String[] fragmentos = tempo.split(":");
        return Integer.parseInt(fragmentos[1]);
    }

    public PreferenciaTempo(Context contexto) {
        this(contexto, null);
    }

    public PreferenciaTempo(Context contexto, AttributeSet atributos) {
        this(contexto, atributos, 0);
    }

    public PreferenciaTempo(Context contexto, AttributeSet atributos, int estilo) {
        super(contexto, atributos, estilo);

        String Ok = contexto.getResources().getString(R.string.btn_confirmar);
        String CANCELAR = contexto.getResources().getString(R.string.btn_cancelar);

        setPositiveButtonText(Ok);
        setNegativeButtonText(CANCELAR);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return picker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        picker.setCurrentHour(ultimaHora);
        picker.setCurrentMinute(ultimoMinuto);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            ultimaHora = picker.getCurrentHour();
            ultimoMinuto = picker.getCurrentMinute();

            String tempo = String.valueOf(ultimaHora) + ":" + String.valueOf(ultimoMinuto);

            if (callChangeListener(tempo)) {
                persistString(tempo);
                Log.i("Script", "Hora salva -> " + tempo);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {

        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String tempo;

        if (restorePersistedValue) {
            if (defaultValue == null) {
                tempo = getPersistedString("00:00");
            } else {
                tempo = getPersistedString(defaultValue.toString());
            }
        } else {
            tempo = defaultValue.toString();
        }

        ultimaHora = obterHora(tempo);
        ultimoMinuto = obterMinuto(tempo);
    }
}