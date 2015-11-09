package br.dev_pedrorjas.mensagemdobem;

/**
 * Created by dev_pedrorjas on 22/07/15.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Notificacao{

    static SharedPreferences preferences;
    static SharedPreferences.Editor editor_preferences;


    private static int dia_ano;
    private static int dia_salvo;
    private static int num_msg_Lista;
    private static int hora;

    private static final String PREF_NAME = "MainActivitiPreferences";
    private static int num_msg_Main;


    private static PendingIntent criarPendingIntent(Context ctx, int id) {

        Intent resultIntent = new Intent(ctx, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        return stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void criarNotification(Context ctx, int idNotificacao) {

        ControleMsg(ctx);

        int icon_nf;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            icon_nf = R.drawable.ic_notification_bk;
        }else {
            icon_nf = R.drawable.ic_notification;
        }


        String mensagem_dia = ctx.getResources().getString(R.string.mensagem_dia);
        String titulo_app = ctx.getResources().getString(R.string.app_name);

        /**Pega preferencias salvas*/

        preferences = ctx.getSharedPreferences(PREF_NAME, 1);

        num_msg_Main = preferences.getInt("NUM_MSG_MAIN", 1);
        Log.i("Script", "num_msg_Lista - Lista Activity" + num_msg_Main);

        /**Pega as mensagens e os titulos*/
        String[] mensagens = ctx.getResources().getStringArray(R.array.mensagens);
        String[] div_mensagem = mensagens[num_msg_Main - 1].split("#");


        /** Pegando datas do sistema */
        Date date = new Date();
        GregorianCalendar dia = new GregorianCalendar();
        dia.setTime(date);
        int dia_corrido = dia.get(GregorianCalendar.DAY_OF_YEAR);
        int ano = dia.get(GregorianCalendar.YEAR);

        /** Corta parte do texto da mensagem para colocar na notificação*/
        String texto = div_mensagem[2];

        String texto_cortado;
        if (texto.length()> 50){
            texto_cortado = texto.substring(0, 50) +"...";
        }else {
            texto_cortado = texto +"...";
        }

        /** Cria a notificação*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(icon_nf)
                .setContentTitle(div_mensagem[0])
                .setSubText(titulo_app)
                .setTicker(titulo_app)
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), icon_nf))
                .setContentIntent(criarPendingIntent(ctx, -1))
                .setStyle(new android.support.v7.app.NotificationCompat.BigTextStyle().bigText(texto_cortado + "\n\n" + mensagem_dia));

        NotificationManagerCompat nm = NotificationManagerCompat.from(ctx);
        Notification n = builder.build();
        //n.vibrate = new long[]{150, 300, 150, 600};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(idNotificacao, n);

        try {
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(ctx, som);
            toque.play();
        } catch (Exception e) {
        }
    }

    public static void ControleMsg(Context ctx){

        preferences = ctx.getSharedPreferences(PREF_NAME, 0);
        editor_preferences = preferences.edit();

        String[] mensagens = ctx.getResources().getStringArray(R.array.mensagens);

        /** Pega datas do sistema */
        Date date = new Date();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        dia_ano = calendar.get(GregorianCalendar.DAY_OF_YEAR);
        Log.i("Script", "dia_ano_Notificação " + dia_ano);

        Date data_atual = calendar.getTime();
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH");
        String hora_atual = dateFormat_hora.format(data_atual);
        hora = Integer.parseInt(hora_atual);

        dia_salvo = preferences.getInt("DIA_SALVO", dia_ano);
        editor_preferences.putInt("DIA_SALVO", dia_salvo);
        Log.i("Script", "dia_salvo_Notificação " + dia_salvo);

        num_msg_Lista = preferences.getInt("NUM_MSG_LISTA", 5);
        editor_preferences.putInt("NUM_MSG_LISTA", num_msg_Lista);
        Log.i("Script", "num_msg_Lista_Notificação " + num_msg_Lista);

        num_msg_Main = preferences.getInt("NUM_MSG_MAIN", 5);
        editor_preferences.putInt("NUM_MSG_MAIN", num_msg_Main);
        Log.i("Script", "num_msg_Main_Notificação " + num_msg_Main);

        editor_preferences.commit();// aplica/salva as alterações nas preferencias

        /**Confere o dia da instalação e adiciona mais um ao contador de mensagem*/
        dia_salvo = preferences.getInt("DIA_SALVO", dia_ano);
        Log.i("Script", "num_msg_Main_Notificação pega dia salvo =  " + dia_salvo);

        if (dia_salvo != dia_ano) {
            Log.i("Script", "diaSalvo_Notificação - > "+ dia_salvo);

            if (num_msg_Main < mensagens.length) {
                num_msg_Main++;
                editor_preferences.putInt("NUM_MSG_MAIN", num_msg_Main);
                Log.i("Script", "commit - num_msg_main soma  +1 = " + num_msg_Main);

                dia_salvo = dia_ano;
                editor_preferences.putInt("DIA_SALVO", dia_salvo);
                Log.i("Script", "commit - dia_salvo recebe = " + dia_salvo);
                editor_preferences.commit();// aplica/salva as alterações nas preferencias
            }else {
                /**Essa condição evita, erro caso numero de msg salvo for maior que array de mensagens*/
                num_msg_Main = 1;

                dia_salvo = dia_ano;
                editor_preferences.putInt("DIA_SALVO", dia_salvo);
                Log.i("Script", "commit - dia_salvo recebe = " + dia_salvo);

                editor_preferences.putInt("NUM_MSG_MAIN", num_msg_Main);
                Log.i("Script", "commit - num_msg_main contador recebe " + num_msg_Main);
                editor_preferences.commit();// aplica/salva as alterações nas preferencias
            }
            if (num_msg_Lista < mensagens.length) {
                num_msg_Lista++;
                editor_preferences.putInt("NUM_MSG_LISTA", num_msg_Lista);
                Log.i("Script", "commit - num_msg_list soma + 1 = " + num_msg_Lista);
                editor_preferences.putInt("NUM_MSG_MAIN", num_msg_Main);
                editor_preferences.commit();// aplica/salva as alterações nas preferencias
            }
        }else {
            num_msg_Main = preferences.getInt("NUM_MSG_MAIN", 1);
        }
    }
}



