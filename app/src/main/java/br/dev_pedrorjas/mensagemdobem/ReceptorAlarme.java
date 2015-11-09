package br.dev_pedrorjas.mensagemdobem;

/**
 * Created by dev_pedrorjas on 22/07/15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ReceptorAlarme extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);
        boolean usarNotificacao = preferencias.getBoolean("alerta", true);

        if (usarNotificacao) {
                Notificacao.criarNotification(context,1);
        } else {
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }
}