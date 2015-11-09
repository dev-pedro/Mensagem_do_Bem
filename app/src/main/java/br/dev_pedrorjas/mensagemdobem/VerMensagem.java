package br.dev_pedrorjas.mensagemdobem;

/**
 * Created by dev_pedrorjas on 22/07/15.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class VerMensagem extends AppCompatActivity {

    private static int LUZ;

    private static final String PREF_NAME = "MainActivitiPreferences";
    SharedPreferences preferences;
    SharedPreferences.Editor editor_preferences;

    public int hora;

    private MenuItem ON;
    private MenuItem OFF;

    String NOME_APP;
    String LINK_APP;

    TextView tv_titulo;
    TextView tv_texto;

    String mostra_Titulo;
    String mostra_Autor;
    String mostra_Texto;
    private Drawable fundo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mensagem);

        final int cor_preta = this.getResources().getColor(R.color.texto_branco);
        final int cor_branca = this.getResources().getColor(R.color.texto_preto);
        final int cor_titulo = this.getResources().getColor(R.color.cor_primaria);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        NOME_APP = getResources().getString(R.string.app_name);
        LINK_APP = getResources().getString(R.string.link_app);

        tv_titulo = (TextView) findViewById(R.id.tv_ver_titulo);
        tv_texto = (TextView) findViewById(R.id.tv_ver_texto);

        Intent intent = getIntent();
        Bundle params = intent.getExtras();

        if(params!=null)
        {
            mostra_Titulo = params.getString("getTitulo");
            mostra_Autor = params.getString("getAutor");
            tv_titulo.setText(mostra_Titulo + "\n" + mostra_Autor);
            mostra_Texto = params.getString("getMensagem");
            tv_texto.setText(mostra_Texto);
        }

        /** Pega datas do sistema*/
        Date date = new Date();
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        Date data_atual = calendar.getTime();
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH");
        String hora_atual = dateFormat_hora.format(data_atual);
        hora = Integer.parseInt(hora_atual);


        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        LUZ = preferences.getInt("LUZ", 1);
        Log.i("Luz", "Ligada " + LUZ);

        /**Carrega o layout conforme a hora do dia*/

        final int DESLIGADA = 0;
        boolean ESTA_NOITE;
        if (hora > 17 || hora < 06 ) {
            ESTA_NOITE = true;
        }else {
            ESTA_NOITE = false;
        }

        if (ESTA_NOITE) {
            final RelativeLayout RL = (RelativeLayout) findViewById(R.id.RelativeLayout_Ver);

            if (LUZ == DESLIGADA) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fundo = getResources().getDrawable(R.drawable.noite, this.getTheme());
                }else {
                    fundo = getResources().getDrawable(R.drawable.noite);
                }

                RL.setBackground(fundo);
                tv_texto.setTextColor(Color.parseColor("#cccccc"));
                tv_titulo.setTextColor(Color.parseColor("#329ed6"));
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fundo = getResources().getDrawable(R.drawable.dia, this.getTheme());
            }else {
                fundo = getResources().getDrawable(R.drawable.dia);
            }

                RL.setBackground(fundo);
                tv_texto.setTextColor(Color.parseColor("#363636"));
                tv_titulo.setTextColor(Color.parseColor("#00649d"));
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ver_mensagem, menu);

        /**Carrega os icones "lâmpada" no periodo da noite*/
        if (hora > 17 || hora < 06 ) {
            if (LUZ == 1) {
                ON = menu.findItem(R.id.on_ver);
                ON.setVisible(true);
                OFF = menu.findItem(R.id.off_ver);
                OFF.setVisible(false);
            }else{
                ON = menu.findItem(R.id.on_ver);
                ON.setVisible(false);
                OFF = menu.findItem(R.id.off_ver);
                OFF.setVisible(true);
            }
        }else {
            ON = menu.findItem(R.id.on_ver);
            ON.setVisible(false);
            OFF = menu.findItem(R.id.off_ver);
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

        //noinspection SimplifiableIfStatement

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor_preferences = preferences.edit();

        if (id == R.id.on_ver){
            final RelativeLayout RL = (RelativeLayout) findViewById(R.id.RelativeLayout_Ver);

            /**Mudar cor de fundo Layout*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fundo = getResources().getDrawable(R.drawable.noite, this.getTheme());
            }else {
                fundo = getResources().getDrawable(R.drawable.noite);
            }

            RL.setBackground(fundo);
            tv_texto.setTextColor(Color.parseColor("#cccccc"));
            tv_titulo.setTextColor(Color.parseColor("#329ed6"));

            ON.setVisible(false);
            OFF.setVisible(true);

            LUZ = 0;
            editor_preferences.putInt("LUZ", LUZ);
            editor_preferences.commit();// aplica/salva as alterações nas preferencias
        }

        if (id == R.id.off_ver){
            final RelativeLayout RL = (RelativeLayout) findViewById(R.id.RelativeLayout_Ver);

            /**Mudar cor de fundo Layout*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fundo = getResources().getDrawable(R.drawable.dia, this.getTheme());
            }else {
                fundo = getResources().getDrawable(R.drawable.dia);
            }

            RL.setBackground(fundo);
            tv_texto.setTextColor(Color.parseColor("#363636"));
            tv_titulo.setTextColor(Color.parseColor("#00649d"));

            ON.setVisible(true);
            OFF.setVisible(false);

            LUZ = 1;
            editor_preferences.putInt("LUZ", LUZ);
            editor_preferences.commit();// aplica/salva as alterações nas preferencias
        }

        if (id == R.id.compartilhar_ver) {
            String app = NOME_APP + " - " + LINK_APP;
            compartilhar(mostra_Titulo + "\n" + mostra_Autor, mostra_Texto, app);
        }

        if (id == R.id.configuar_ver){
            Intent intent = new Intent();
            intent.setClass(VerMensagem.this, EdicaoPreferencias.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Lista_Mensagem.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        VerMensagem.super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void compartilhar(String titulo, String texto, String app){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, titulo + "\n" + texto + "\n\n" + app);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        return;
    }
}
