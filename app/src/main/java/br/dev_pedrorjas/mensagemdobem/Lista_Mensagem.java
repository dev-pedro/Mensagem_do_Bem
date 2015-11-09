package br.dev_pedrorjas.mensagemdobem;

/**
 * Created by dev_pedrorjas on 22/07/15.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

public class Lista_Mensagem extends AppCompatActivity {

    public static final String PREF_NAME = "MainActivitiPreferences";
    private static int num_msg_Lista;

    SharedPreferences preferences;
    SharedPreferences.Editor editor_preferences;

    private int LUZ;
    final int DESLIGADA = 0;
    final int LIGADA = 1;
    public int hora;

    private MenuItem ON;
    private MenuItem OFF;

    ListView listView;
    ArrayList<String> lista;

    String NOME_APP;
    String LINK_APP;

    private AlertDialog alerta;
    private AlertDialog infoAlerta;

    ArrayAdapter<String> adapter;

    private Drawable fundo;
    private int cor_titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**Adiciona icone do app na BarActivity*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);

        cor_titulo = this.getResources().getColor(R.color.cor_primaria);

        setContentView(R.layout.activity_lista_mensagem);

        listView = (ListView) findViewById(R.id.listView_lista_mensagem);

        PrimeiroAvisoAletaLista();

        lista = new ArrayList();
        NOME_APP = getResources().getString(R.string.app_name);
        LINK_APP = getResources().getString(R.string.link_app);

        /**Pega as mensagens e os titulos*/
        final String[] mensagens = getResources().getStringArray(R.array.mensagens);
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        num_msg_Lista = preferences.getInt("NUM_MSG_LISTA", 1);
        Log.i("Script", "num_msg_Lista - Lista Activity " + num_msg_Lista);

        if (num_msg_Lista > mensagens.length){
            num_msg_Lista = mensagens.length;

            Log.i("Script", "num_msg_list alterado " + num_msg_Lista);
        }

        int i = 0;
        String[] div_mensagem;
        final String[] titulos = new String[num_msg_Lista];
        final String[] temp_msg = new String[num_msg_Lista];
        while (i < num_msg_Lista){
            div_mensagem = mensagens[i].split("#");
            temp_msg[i] = mensagens[i];
            titulos[i] = div_mensagem[0];
            i ++;
        }

        Arrays.sort(temp_msg);
        Arrays.sort(titulos);//Coloca a lista em ordem alfabetica

        /**Lista as mensagens*/
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titulos){

            @Override
            public View getView (int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                // define a cor do texto na listview
                ((TextView) view).setTextColor(cor_titulo);
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String BTN_COMPARTILHAR = getResources().getString(R.string.btn_compartilhar);
                String BTN_LER = getResources().getString(R.string.btn_ler_msg);

                final String[] mensagem = temp_msg[position].split("#");

                /** Corta parte do texto da mensagem para colocar no Alerta*/
                final String titulo = mensagem[0];
                final String autor = mensagem[1];
                final String texto = mensagem[2];

                String texto_cortado;
                if (texto.length() > 80){
                    texto_cortado = texto.substring(0, 80) +"...";
                }else {
                    texto_cortado = texto +"...";
                }

                //Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Lista_Mensagem.this);
                //define icone
                builder.setIcon(R.drawable.ic_launcher);
                //define o mensagens
                builder.setTitle(titulo + "\n" + autor);
                //define a mensagem
                builder.setMessage(texto_cortado);
                //define um botão como positivo
                builder.setPositiveButton(BTN_COMPARTILHAR, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        String app = NOME_APP + " - " + LINK_APP;
                        compartilhar(titulo + "\n" + autor, texto, app);
                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton(BTN_LER, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Lista_Mensagem.this, VerMensagem.class);
                        Bundle params = new Bundle();

                        String enviar_Titulo = titulo;
                        String enviar_Autor = autor;
                        String enviar_Texto = texto;

                        params.putString("getTitulo", enviar_Titulo);
                        params.putString("getAutor", enviar_Autor);
                        params.putString("getMensagem", enviar_Texto);

                        intent.putExtras(params);
                        startActivity(intent);
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

            }
        });

        /** Pega datas do sistema*/
        Date date = new Date();
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        Date data_atual = calendar.getTime();
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH");
        String hora_atual = dateFormat_hora.format(data_atual);
        hora = Integer.parseInt(hora_atual);

        /**Carrega as preferencias do usuário*/
        LUZ = preferences.getInt("LUZ", 1);
        Log.i("Luz", "Ligada " + LUZ);


        boolean ESTA_NOITE;
        if (hora > 17 || hora < 06 ) {
            ESTA_NOITE = true;
        }else {
            ESTA_NOITE = false;
        }

        /**Carrega o layout conforme a hora do dia*/
        if (ESTA_NOITE) {
            final RelativeLayout RL = (RelativeLayout) findViewById(R.id.Relative_Layout_Lista);

            if (LUZ == DESLIGADA) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fundo = getResources().getDrawable(R.drawable.noite, this.getTheme());
                }else {
                    fundo = getResources().getDrawable(R.drawable.noite);
                }

                RL.setBackground(fundo);

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fundo = getResources().getDrawable(R.drawable.dia, this.getTheme());
                }else {
                    fundo = getResources().getDrawable(R.drawable.dia);
                }
                RL.setBackground(fundo);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_mensagem, menu);

        /**Carrega os icones "lâmpada" no periodo da noite*/
        if (hora > 17 || hora < 06 ) {
            if (LUZ == LIGADA) {
                ON = menu.findItem(R.id.on_lista);
                ON.setVisible(true);
                OFF = menu.findItem(R.id.off_lista);
                OFF.setVisible(false);
            }else{
                ON = menu.findItem(R.id.on_lista);
                ON.setVisible(false);
                OFF = menu.findItem(R.id.off_lista);
                OFF.setVisible(true);
            }
        }else {
            ON = menu.findItem(R.id.on_lista);
            ON.setVisible(false);
            OFF = menu.findItem(R.id.off_lista);
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

        if (id == R.id.on_lista){
            final RelativeLayout RL = (RelativeLayout) findViewById(R.id.Relative_Layout_Lista);

            /**Mudar cor de fundo Layout*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fundo = getResources().getDrawable(R.drawable.noite, this.getTheme());
            }else {
                fundo = getResources().getDrawable(R.drawable.noite);
            }

            RL.setBackground(fundo);
            ON.setVisible(false);
            OFF.setVisible(true);

            LUZ = 0;
            editor_preferences.putInt("LUZ", LUZ);
            editor_preferences.commit();// aplica/salva as alterações nas preferencias
        }

        if (id == R.id.off_lista){
            final RelativeLayout RL = (RelativeLayout) findViewById(R.id.Relative_Layout_Lista);

            /**Mudar cor de fundo Layout*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fundo = getResources().getDrawable(R.drawable.dia, this.getTheme());
            }else {
                fundo = getResources().getDrawable(R.drawable.dia);
            }
            RL.setBackground(fundo);

            ON.setVisible(true);
            OFF.setVisible(false);

            LUZ = 1;
            editor_preferences.putInt("LUZ", LUZ);
            editor_preferences.commit();// aplica/salva as alterações nas preferencias
        }

        if (id == R.id.configuar_list){
            Intent intent = new Intent();
            intent.setClass(Lista_Mensagem.this, EdicaoPreferencias.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void compartilhar(String titulo, String texto, String app){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, titulo + "\n" + texto + "\n\n" + app);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        return;
    }

    public void PrimeiroAvisoAletaLista(){

        //cria e infla check box para colocar no Alerta Dialog
        LayoutInflater adbInflater = LayoutInflater.from(Lista_Mensagem.this);
        View view_checkBox = adbInflater.inflate(R.layout.check_box, null);
        final CheckBox checkBox = (CheckBox)view_checkBox.findViewById(R.id.checkBox);

        //recupera as preferencias para mostra a caixa de diálogo
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor_preferences = preferences.edit();
        int alert_cont_lista = preferences.getInt("ALERT_LISTA", 0);

        if (alert_cont_lista != 1) {

            String AppName = getResources().getString(R.string.app_name);
            String texto = getResources().getString(R.string.info_lista_mensagem);

            //Cria o gerador do AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(Lista_Mensagem.this);
            //define check box
            builder.setView(view_checkBox);
            //define botão OK
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    if (checkBox.isChecked()) {

                        /**adiciona o valor 1 a variavel - lista_cont - para evitar loop do AlertaDialog*/
                        int checado = 1;
                        editor_preferences.putInt("ALERT_LISTA", checado);
                        Log.i("Script", "ALERT_CONT_LISTA " + checado);
                        editor_preferences.commit();// aplica/salva as alterações nas preferencias
                    }else {

                    }
                }
            });
            //define botão cancelar
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Lista_Mensagem.super.onBackPressed();
    }

}
