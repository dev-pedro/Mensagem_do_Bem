package br.dev_pedrorjas.mensagemdobem;

/**
 * Created by dev_pedrorjas on 22/07/15.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;


public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MainActivitiPreferences";

    SharedPreferences preferences;
    SharedPreferences.Editor editor_preferences;

    TextView tv_msg;
    TextView tv_titulo;

    String NOME_APP;
    String LINK_APP;
    String EMAIL_CONTATO;
    private AlertDialog infoAlerta;

    private  int dia_salvo;
    private int num_msg_Lista;
    private int num_msg_Main;

    private MenuItem ON;
    private MenuItem OFF;
    private int LUZ;
    private int DESLIGADA = 0;
    private final int LIGADA = 1;
    protected int hora;
    private Drawable fundo;
    private int cor_titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**Adiciona icone do app na BarActivity*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        cor_titulo = this.getResources().getColor(R.color.cor_primaria);

        PrimeiroAvisoAleta();
        ChangeLog_First_Run();
        ReceptorBoot.configurarAlarme(MainActivity.this);

        /**Carrega as mensagens e os titulos nas variaveis*/
        NOME_APP = getResources().getString(R.string.app_name);
        LINK_APP = getResources().getString(R.string.link_app);
        EMAIL_CONTATO = getResources().getString(R.string.email_contato);
        String[] mensagens = getResources().getStringArray(R.array.mensagens);

        /**Carrega as váriáveis TextView*/
        tv_msg = (TextView) findViewById(R.id.tv_mensagem);
        tv_titulo = (TextView) findViewById(R.id.tv_titulo);

        /** Pega datas do sistema */
        Date date = new Date();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        Date data_atual = calendar.getTime();
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH");
        String hora_atual = dateFormat_hora.format(data_atual);
        hora = Integer.parseInt(hora_atual);

        Notificacao.ControleMsg(MainActivity.this);

        /** recupera (ou cria) uma instância de preferencia do Android, pelo seu nome/chave (no caso "PREF_NAME")*/
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor_preferences = preferences.edit();
        dia_salvo = preferences.getInt("DIA_SALVO", 1);
        Log.i("Script", "dia_salvo " + dia_salvo);
        num_msg_Lista = preferences.getInt("NUM_MSG_LISTA", 1);
        Log.i("Script", "num_msg_Lista " + num_msg_Lista);
        num_msg_Main = preferences.getInt("NUM_MSG_MAIN", 1);
        Log.i("Script", "num_msg_main " + num_msg_Main);
        LUZ = preferences.getInt("LUZ", 1);
        Log.i("Luz", "Ligada " + LUZ);


        boolean ESTA_NOITE;
        if (hora > 17 || hora < 06 ) {
            ESTA_NOITE = true;
        }else {
            ESTA_NOITE = false;
        }

        if (ESTA_NOITE) {
            final RelativeLayout RL = (RelativeLayout) findViewById(R.id.RelativeLayout1);

            if (LUZ == DESLIGADA) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fundo = getResources().getDrawable(R.drawable.noite, this.getTheme());
                }else {
                    fundo = getResources().getDrawable(R.drawable.noite);
                }
                RL.setBackground(fundo);
                tv_msg.setTextColor(this.getResources().getColor(R.color.texto_branco));
                tv_titulo.setTextColor(this.getResources().getColor(R.color.cor_primaria));
                LUZ = DESLIGADA;
                editor_preferences.putInt("LUZ", LUZ);
                Log.i("Luz", "Main - Estado - Desl = 0 -> " + LUZ);

                editor_preferences.commit();// aplica/salva as alterações nas preferencias
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fundo = getResources().getDrawable(R.drawable.dia, this.getTheme());
                }else {
                    fundo = getResources().getDrawable(R.drawable.dia);
                }

                RL.setBackground(fundo);
                tv_msg.setTextColor(this.getResources().getColor(R.color.texto_preto));
                tv_titulo.setTextColor(this.getResources().getColor(R.color.cor_primaria));

                LUZ = LIGADA;
                editor_preferences.putInt("LUZ", LUZ);
                Log.i("Luz", "Main - Estado - Lig = 1 -> " + LUZ);

                editor_preferences.commit();// aplica/salva as alterações nas preferencias
            }
        }

        Log.i("Script", "O que deu errado = " + num_msg_Main);
        String[] div_mensagem = mensagens[num_msg_Main - 1].split("#");
        String titulo = div_mensagem[0];
        String autor = div_mensagem[1];
        String texto = div_mensagem[2];
        /**Apresanta a mensagem no TextView conforme o dia*/

        tv_titulo.setText(titulo + "\n" + autor);
        tv_msg.setText(texto);

    }

    public void compartilhar(String titulo, String texto, String app){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, titulo + "\n" + texto + "\n\n" + app);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /**Carrega os icones "lâmpada" no periodo da noite*/
        if (hora > 17 || hora < 06 ) {
            if (LUZ == 1) {
                ON = menu.findItem(R.id.on_main);
                ON.setVisible(true);
                OFF = menu.findItem(R.id.off_main);
                OFF.setVisible(false);
            }else{
                ON = menu.findItem(R.id.on_main);
                ON.setVisible(false);
                OFF = menu.findItem(R.id.off_main);
                OFF.setVisible(true);
            }
        }else {
            ON = menu.findItem(R.id.on_main);
            ON.setVisible(false);
            OFF = menu.findItem(R.id.off_main);
            OFF.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.on_main){
            /**Mudar cor de fundo Layout*/
            ModoNoturno();
        }

        if (id == R.id.off_main){
            /**Mudar cor de fundo Layout*/
            ModoNoturno();
        }


        if (id == R.id.configuar){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, EdicaoPreferencias.class);
            startActivity(intent);
        }

        if (id == R.id.compartilhar){

            /** Pegando datas do sistema */
            Date date = new Date();
            GregorianCalendar dia = new GregorianCalendar();
            dia.setTime(date);

            String app = NOME_APP + " - " + LINK_APP;
            String[] mensagens = getResources().getStringArray(R.array.mensagens);
            String[] div_mensagem = mensagens[num_msg_Main - 1].split("#");
            String titulo = div_mensagem[0];
            String autor = div_mensagem[1];
            String texto = div_mensagem[2];
            compartilhar(titulo + "\n" + autor, texto, app);
        }

        if (id == R.id.buscar) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Lista_Mensagem.class);
            startActivity(intent);
        }

        if (id == R.id.sobre){

            String AVALIAR = getResources().getString(R.string.btn_avaliar);
            String BTN_CONTATO = getResources().getString(R.string.btn_contato);

            String version = BuildConfig.VERSION_NAME;
            String VERSIONNAME = getResources().getString(R.string.version) + version;

            final String AppName = getResources().getString(R.string.app_name);
            String texto_sobre = getResources().getString(R.string.texto_sobre);
            String developer = getResources().getString(R.string.developer);


            //Cria o gerador do AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            //define botão OK
            builder.setPositiveButton(BTN_CONTATO, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {

                    String[] contato = {EMAIL_CONTATO};
                    Intent email = new Intent(Intent.ACTION_SEND);

                    email.putExtra(Intent.EXTRA_EMAIL, contato);
                    email.putExtra(Intent.EXTRA_SUBJECT, AppName + " :");
                    email.setType("plain/text");
                    startActivity(Intent.createChooser(email, ""));
                }
            });
            //define botão cancelar
            builder.setNegativeButton(AVALIAR, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Uri uri = Uri.parse(LINK_APP);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            builder.setIcon(R.drawable.ic_launcher);
            //define o mensagens
            builder.setTitle(AppName + "\n" + VERSIONNAME);
            //define a mensagem
            builder.setMessage(texto_sobre + "\n" + developer);
            //cria o AlertDialog
            infoAlerta = builder.create();
            //Exibe
            infoAlerta.show();
        }

        return super.onOptionsItemSelected(item);
    }


    /**Metodo chamado quando a Activity é fechada*/
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    public void onBackPressed() {

        //Toast.makeText(MainActivity.this, "Saindo", Toast.LENGTH_SHORT).show();
        MainActivity.super.onBackPressed();
        /*new AlertDialog.Builder(this)
                .setTitle("Sair")
                .setMessage("Você tem certeza de que quer sair ?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();*/
    }

    public void ModoNoturno(){

        final RelativeLayout RL = (RelativeLayout) findViewById(R.id.RelativeLayout1);

        if (LUZ == LIGADA) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fundo = getResources().getDrawable(R.drawable.noite, this.getTheme());
            }else {
                fundo = getResources().getDrawable(R.drawable.noite);
            }

            RL.setBackground(fundo);
            tv_msg.setTextColor(this.getResources().getColor(R.color.texto_branco));
            tv_titulo.setTextColor(this.getResources().getColor(R.color.cor_primaria));

            ON.setVisible(false);
            OFF.setVisible(true);
            LUZ = DESLIGADA;
            Log.i("Luz", "Desligada " + LUZ);

            editor_preferences = preferences.edit();
            editor_preferences.putInt("LUZ", LUZ);
            Log.i("Luz", "Metodo noturno - Estado - Desl = 0 -> " + LUZ);
            editor_preferences.commit();// aplica/salva as alterações nas preferencias
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fundo = getResources().getDrawable(R.drawable.dia, this.getTheme());
            }else {
                fundo = getResources().getDrawable(R.drawable.dia);
            }

            RL.setBackground(fundo);
            tv_msg.setTextColor(this.getResources().getColor(R.color.texto_preto));
            tv_titulo.setTextColor(this.getResources().getColor(R.color.cor_primaria));

            ON.setVisible(true);
            OFF.setVisible(false);
            LUZ = LIGADA;
            Log.i("Luz", "Ligada " + LUZ);

            editor_preferences = preferences.edit();
            editor_preferences.putInt("LUZ", LUZ);
            Log.i("Luz", "Metodo noturno - Estado - Lig = 1 -> " + LUZ);
            editor_preferences.commit();// aplica/salva as alterações nas preferencias
        }

    }



    public void PrimeiroAvisoAleta(){

        String OK = getResources().getString(R.string.btn_confirmar);
        String CANCEL = getResources().getString(R.string.btn_cancelar);

        //cria e infla check box para colocar no Alerta Dialog
        LayoutInflater adbInflater = LayoutInflater.from(MainActivity.this);
        View view_checkBox = adbInflater.inflate(R.layout.check_box, null);
        final CheckBox checkBox = (CheckBox)view_checkBox.findViewById(R.id.checkBox);

        //recupera as preferencias para mostra a caixa de diálogo
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor_preferences = preferences.edit();
        int alert_cont_main = preferences.getInt("ALERT_MAIN", 0);

        if (alert_cont_main != 1) {

            String AppName = getResources().getString(R.string.app_name);
            String texto = getResources().getString(R.string.info_main_mensagem);

            //Cria o gerador do AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            //define check box
            builder.setView(view_checkBox);
            //define botão OK
            builder.setPositiveButton(OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    if (checkBox.isChecked()) {
                        /**adiciona o valor 1 a variavel - lista_cont - para evitar loop do AlertaDialog*/
                        int checado = 1;
                        editor_preferences.putInt("ALERT_MAIN", checado);
                        Log.i("Script", "ALERT_MAIN " + checado);
                        editor_preferences.commit();// aplica/salva as alterações nas preferencias
                    }
                }
            });
            //define botão cancelar
            builder.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            builder.setIcon(R.drawable.ic_launcher);
            //define o mensagens
            builder.setTitle(AppName);
            //define a mensagem
            builder.setMessage(texto + "\n");
            //cria o AlertDialog
            infoAlerta = builder.create();
            //Exibe
            infoAlerta.show();
        }else {
            return;
        }
    }

    private void ChangeLog_First_Run() {
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (preferences.getBoolean("isFirstRun", true)) {
            ChangeLog();

            editor_preferences = preferences.edit();
            editor_preferences.putBoolean("isFirstRun", false);
            editor_preferences.commit();
        }
    }

    public void ChangeLog(){
        //Launch change log dialog
        ChangeLogDialog _ChangelogDialog = new ChangeLogDialog(this);
        _ChangelogDialog.show();
    }

}
