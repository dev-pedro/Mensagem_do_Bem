package br.dev_pedrorjas.mensagemdobem;

/**
 * Created by dev_pedrorjas on 22/07/15.
 */

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class EdicaoPreferencias extends Activity {

    SharedPreferences preferencias = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        PreferenciasFragment mPreferenciasFragment = new PreferenciasFragment();
        mFragmentTransaction.replace(android.R.id.content, mPreferenciasFragment);
        mFragmentTransaction.commit();

    }

    @Override
    protected void onResume() {
        preferencias = PreferenceManager.getDefaultSharedPreferences(EdicaoPreferencias.this);
        preferencias.registerOnSharedPreferenceChangeListener(onChange);
        super.onResume();
    }

    @Override
    protected void onPause() {
        preferencias.unregisterOnSharedPreferenceChangeListener(onChange);
        super.onPause();
    }

    OnSharedPreferenceChangeListener onChange = new SharedPreferences.OnSharedPreferenceChangeListener() {


        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(EdicaoPreferencias.this);
            String horario = preferencias.getString("horario_alarme", "10:00");

            if ("alerta".equals(key)) {
                boolean habilitado = preferencias.getBoolean(key, false);
                int flag = (habilitado ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
                ComponentName componente = new ComponentName(EdicaoPreferencias.this, ReceptorBoot.class);

                getPackageManager().setComponentEnabledSetting(componente, flag, PackageManager.DONT_KILL_APP);

                if (habilitado) {
                    ReceptorBoot.configurarAlarme(EdicaoPreferencias.this);


                    Toast.makeText(EdicaoPreferencias.this, "Alarme definido para - "+horario,Toast.LENGTH_LONG).show();
                } else {
                    ReceptorBoot.cancelarAlarme(EdicaoPreferencias.this);
                }
            } else if ("horario_alarme".equals(key)) {
                ReceptorBoot.cancelarAlarme(EdicaoPreferencias.this);
                ReceptorBoot.configurarAlarme(EdicaoPreferencias.this);
                Toast.makeText(EdicaoPreferencias.this, "Alarme definido para - " + horario, Toast.LENGTH_LONG).show();
            }
        }
    };

    public static class PreferenciasFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferencias);
        }
    }
}