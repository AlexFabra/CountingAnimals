package cat.dam.alex.countinganimals;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ArrayList <ImageView> imatges;
    private ImageView iv1a,iv2a,iv3a,iv4a,iv1b,iv2b,iv3b,iv4b;
    private final String[] imgResources ={"i00","i01","i02","i03","i04","i05","i06","i07","i08","i09","i10"};
    private Random nAleatori = new Random();
    //sincronizamos la puntuación:
    private TextView tv_punts;
    private int numDreta = 0;
    private int numEsquerra = 0;
    Handler handler = new Handler(); //per pausar
    Runnable runnable;
    private int punts;
    private static final String PUNTUACIO = "playerScore";
    boolean guanya=false;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Desa l’estat actual del joc de l’usuari
        savedInstanceState.putInt(PUNTUACIO, punts);
        // Sempre cridem a la superclasse perquè desi també la jerarquia de vistes actual
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onResume(){
        super.onResume();
        //para guardar la puntuación:
        //creem una variable per guardar la dada al layout:
        TextView tv_punts = (TextView) findViewById(R.id.tv_punts);
        //guardamos el casting String de puntuación:
        String puntuacionS = String.valueOf(punts);
        //lo guardamos en el elemento del layout:
        tv_punts.setText(puntuacionS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            // Restaura els valors que s’han desat de l’estat
            punts = savedInstanceState.getInt(PUNTUACIO);
            onResume();
        }

        imatges= new ArrayList<ImageView>();
        tv_punts = (TextView) findViewById(R.id.tv_punts);
        imatges=inclouIvEnArrayList(imatges);
        imatges=randomitzaImatges(imatges);

        //cada segón es comprobara si guanya és true
        handler.postDelayed(runnable = new Runnable(){
            public void run(){
                if(guanya==true){
                    numDreta=0;
                    numEsquerra=0;
                    imatges=randomitzaImatges(imatges);
                    guanya=false;
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    /** inclouIvEnArrayList afegeix els ImageViews a una ArrayList
     * @param imgs ArrayList d'objectes ImageView
     * @return ArrayList d'objectes ImageView amb objectes.
     */
    public ArrayList<ImageView> inclouIvEnArrayList(ArrayList<ImageView> imgs){
        //sincronitzem els ImageViews

        iv1a= findViewById(R.id.img_esquerra_r1c1);
        iv2a=findViewById(R.id.img_esquerra_r1c2);
        iv3a=findViewById(R.id.img_esquerra_r2c1);
        iv4a=findViewById(R.id.img_esquerra_r2c2);
        iv1b=findViewById(R.id.img_dreta_r1c1);
        iv2b=findViewById(R.id.img_dreta_r1c2);
        iv3b=findViewById(R.id.img_dreta_r2c1);
        iv4b=findViewById(R.id.img_dreta_r2c2);

        imgs.add(iv1a);imgs.add(iv2a);imgs.add(iv3a);imgs.add(iv4a);
        imgs.add(iv1b);imgs.add(iv2b);imgs.add(iv3b);imgs.add(iv4b);

        return imgs;
    }

    /** randomitzaImatges randomitza les imatges que surten al layout i
     *  actualitza els contadors de cada costat:
     * @param imgs ArrayList<ImageView>
     * @return ArrayList<ImageView>
     */
    public ArrayList<ImageView> randomitzaImatges(ArrayList<ImageView> imgs){
        String randomImgResurce="";
        int nombreRandom;
//        for ( ImageView img:imgs) {
//            nombreRandom=nAleatori.nextInt(imgResources.length);
//            randomImgResurce=imgResources[nombreRandom];
//            img=creaImagen(img,randomImgResurce);
//        }
        for (int i=0;i<imgs.size();i++){
            nombreRandom=nAleatori.nextInt(imgResources.length);
            randomImgResurce=imgResources[nombreRandom];

            //para que cada lado tenga su numero de animales:
            if(imgs.indexOf(imgs.get(i))>4) {
                numEsquerra+=nombreRandom;
            }else {
                numDreta+=nombreRandom;
            }
            Drawable drawable = StringToDrawable(randomImgResurce);
            imgs.get(i).setImageDrawable(drawable);
        }
        return imgs;
    }

    /** creaImagen inyecta una imagen a un ImageView.
     * @param iv ImageView
     * @param nombreImagen String nombre de la imagen
     */
    public ImageView creaImagen(ImageView iv, String nombreImagen) {
        Drawable imagen = StringToDrawable(nombreImagen);
        iv.setImageDrawable(imagen);
        return iv;
    }

    /** String to Drawable crea un Drawable de un String.
     * @param nombreImagen String
     * @return Drawable
     */
    public Drawable StringToDrawable(String nombreImagen){
        //guardamos los resources en esta variable para poder acceder a ellos:
        Resources res = this.getResources();
        //encontramos el que nos interesa:
        int resId = res.getIdentifier(nombreImagen, "drawable", this.getPackageName());
        //Declaramos la variable de tipo drawable y le pasamos el identificador:
        return getResources().getDrawable(resId);
    }

    /** gestionaResposta comproba si s'ha guanyat i envia el resultat a actualitzaPartida
     * @param v View
     */
    public void gestionaResposta (View v){
        int resposta;
        //guardem el nom del id:
        String sView = v.getResources().getResourceName(v.getId());
        System.out.println(sView);
        if (sView.contains("greater")){resposta=1; actualitzaPartida(resposta);}
        else if (sView.contains("equal")){resposta=0; actualitzaPartida(resposta);}
        else {resposta=-1; actualitzaPartida(resposta);}
    }

    /** actualitzaPartida actualitza els punts segons si s'ha guanyat o perdut.
     * @param resposta int
     */
    public void actualitzaPartida(int resposta){
        switch(resposta){
            case 1: //més a la esquerra?
                if(numEsquerra>numDreta){
                    guanyarPunt();
                    mostraFelicitació();
                } else {
                    mostraError();
                    perdrePunts();
                }
                break;
            case 0: //igual
                if(numEsquerra==numDreta){
                    guanyarPunt();
                    mostraFelicitació();
                } else {
                    mostraError();
                    perdrePunts();
                }
                break;
            case -1: //més a la dreta?
                if(numDreta>numEsquerra){
                    guanyarPunt();
                    mostraFelicitació();
                } else {
                    mostraError();
                    perdrePunts();
                }
                break;
        }
    }
    /** guanyarPunt suma 1 als punts i actualitza el tv_punts:
     */
    public void guanyarPunt(){
        punts++;
        tv_punts.setText(String.valueOf(punts));
        guanya=true;
    }

    /** perdrePunts reinicia els punts i actualitza el tv_punts:
     */
    public void perdrePunts(){
        punts=0;
        tv_punts.setText(String.valueOf(punts));
    }
    /** mostraFelicitació crea un toast indicant que s'ha encertat:
     */
    public void mostraFelicitació(){
        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.felicitacio), Toast.LENGTH_SHORT).show();
    }

    /** mostraError crea un toast indicant que s'ha errat:
     */
    public void mostraError(){
        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    /** sortir ha de preguntar a l'usuari si realment vol sortir i d'enviar.lo a altre layout en cas que sí.
     * @param v View
     */
    public void sortir (View v){

    }
}