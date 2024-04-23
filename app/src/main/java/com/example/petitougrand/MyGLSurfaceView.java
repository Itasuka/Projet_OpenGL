/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.petitougrand;

import android.content.Context;
import android.opengl.EGLConfig;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/* La classe MyGLSurfaceView avec en particulier la gestion des événements
  et la création de l'objet renderer

*/


/* On va dessiner un carré qui peut se déplacer grâce à une translation via l'écran tactile */

public class MyGLSurfaceView extends GLSurfaceView {

    /* Un attribut : le renderer (GLSurfaceView.Renderer est une interface générique disponible) */
    /* MyGLRenderer va implémenter les méthodes de cette interface */

    private MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        // Création d'un context OpenGLES 3.0
        setEGLContextClientVersion(3);

        // Création du renderer qui va être lié au conteneur View créé
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Option pour indiquer qu'on redessine uniquement si les données changent
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    /* pour gérer la translation */
    private float mPreviousX;
    private float mPreviousY;
    private boolean condition = false;

    /* Comment interpréter les événements sur l'écran tactile */
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        // Les coordonnées du point touché sur l'écran
        float x = e.getX();
        float y = e.getY();

        // la taille de l'écran en pixels
        float screen_x = getWidth();
        float screen_y = getHeight();



        // Des messages si nécessaires */
        Log.d("message", "x"+Float.toString(x));
        Log.d("message", "y"+Float.toString(y));
        Log.d("message", "screen_x="+Float.toString(screen_x));
        Log.d("message", "screen_y="+Float.toString(screen_y));


        /* accès aux paramètres du rendu (cf MyGLRenderer.java)
        soit la position courante du centre du carré
         */
        float[] pos = mRenderer.getPosition();

        /* Conversion des coordonnées pixel en coordonnées OpenGL
        Attention l'axe x est inversé par rapport à OpenGLSL
        On suppose que l'écran correspond à un carré d'arête 2 centré en 0
         */

        float x_opengl = 20.0f*x/getWidth() - 10.0f;
        float y_opengl = -20.0f*y/getHeight() + 10.0f;

        Log.d("message","x_opengl="+Float.toString(x_opengl));
        Log.d("message","y_opengl="+Float.toString(y_opengl));

        /* Le carré représenté a une arête de 2 (oui il va falloir changer cette valeur en dur !!)
        /* On teste si le point touché appartient au carré ou pas car on ne doit le déplacer que si ce point est dans le carré
        */

       //boolean test_square = ((x_opengl < pos[0]+1.0) && (x_opengl > pos[0]-1.0) && (y_opengl < pos[1]+1.0) && (y_opengl > pos[1]-1.0));
       float[] bordureBoutonPlus = mRenderer.getBoutonPlus().minMaxCoords();
       boolean test_plus = ((x_opengl < bordureBoutonPlus[1]) && (x_opengl > bordureBoutonPlus[0]) && (y_opengl < bordureBoutonPlus[3]) && (y_opengl > bordureBoutonPlus[2]));
       float[] bordureBoutonMoins = mRenderer.getBoutonMoins().minMaxCoords();
       boolean test_moins = ((x_opengl < bordureBoutonMoins[1]) && (x_opengl > bordureBoutonMoins[0]) && (y_opengl < bordureBoutonMoins[3]) && (y_opengl > bordureBoutonMoins[2]));
       float[] bordureBoutonEgale = mRenderer.getBoutonEgale().minMaxCoords();
       boolean test_egale = ((x_opengl < bordureBoutonEgale[1]) && (x_opengl > bordureBoutonEgale[0]) && (y_opengl < bordureBoutonEgale[3]) && (y_opengl > bordureBoutonEgale[2]));
       float[] bordureBoutonPasserTour = mRenderer.getBoutonPasserTour().minMaxCoords();
       boolean test_passerTour = ((x_opengl < bordureBoutonPasserTour[1]) && (x_opengl > bordureBoutonPasserTour[0]) && (y_opengl < bordureBoutonPasserTour[3]) && (y_opengl > bordureBoutonPasserTour[2]));
       float[] bordureBoutonAideCartes = mRenderer.getBoutonAideCartes().minMaxCoords();
       boolean test_aideCartes = ((x_opengl < bordureBoutonAideCartes[1]) && (x_opengl > bordureBoutonAideCartes[0]) && (y_opengl < bordureBoutonAideCartes[3]) && (y_opengl > bordureBoutonAideCartes[2]));
       float[] bordureBoutonRegles = mRenderer.getBoutonRegles().minMaxCoords();
       boolean test_regles = ((x_opengl < bordureBoutonRegles[1]) && (x_opengl > bordureBoutonRegles[0]) && (y_opengl < bordureBoutonRegles[3]) && (y_opengl > bordureBoutonRegles[2]));
        Log.d("Bouton", "minX = "+ bordureBoutonPlus[0] + "; maxX = "+bordureBoutonPlus[1]+"; minY = "+bordureBoutonPlus[2]+"; maxY = "+bordureBoutonPlus[3]);
        Log.d("Bouton", "minX = "+ bordureBoutonMoins[0] + "; maxX = "+bordureBoutonMoins[1]+"; minY = "+bordureBoutonMoins[2]+"; maxY = "+bordureBoutonMoins[3]);
        Log.d("Bouton", "minX = "+ bordureBoutonEgale[0] + "; maxX = "+bordureBoutonEgale[1]+"; minY = "+bordureBoutonEgale[2]+"; maxY = "+bordureBoutonEgale[3]);
        Log.d("Bouton", "minX = "+ bordureBoutonPasserTour[0] + "; maxX = "+bordureBoutonPasserTour[1]+"; minY = "+bordureBoutonPasserTour[2]+"; maxY = "+bordureBoutonPasserTour[3]);
        Log.d("Bouton", "minX = "+ bordureBoutonAideCartes[0] + "; maxX = "+bordureBoutonAideCartes[1]+"; minY = "+bordureBoutonAideCartes[2]+"; maxY = "+bordureBoutonAideCartes[3]);
        Log.d("Bouton", "minX = "+ bordureBoutonRegles[0] + "; maxX = "+bordureBoutonRegles[1]+"; minY = "+bordureBoutonRegles[2]+"; maxY = "+bordureBoutonRegles[3]);


        //Log.d("message","test_square="+Boolean.toString(test_square));
        Log.d("message","condition="+Boolean.toString(condition));

        if (test_plus){
            switch (e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("Touche", "Touche le bouton plus");
                    break;
            }
        }

        if (test_moins){
            switch (e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("Touche", "Touche le bouton moins");
                    break;
            }
        }

        if (test_egale){
            switch (e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("Touche", "Touche le bouton egale");
                    break;
            }
        }

        if (test_passerTour){
            switch (e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("Touche", "Touche le bouton passer le tour");
                    break;
            }
        }

        if (test_aideCartes){
            switch (e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("Touche", "Touche le bouton aide des cartes");
                    break;
            }
        }

        if (test_regles){
            switch (e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("Touche", "Touche le bouton regles");
                    break;
            }
        }

        /*if (condition || test_square) {

            switch (e.getAction()) {
                // Lorsqu'on touche l'écran on mémorise juste le point
                case MotionEvent.ACTION_DOWN:
                    mPreviousX = x;
                    mPreviousY = y;
                    condition=true;
                    break;
                case MotionEvent.ACTION_UP:
                    changeForme(0,1);
                    changeForme(3,-1);
                    //Forme formeBasse = new Forme(5,-1);
                    //mRenderer.setmFormeHaute(formeHaute);
                    //mRenderer.setmFormeBasse(formeBasse);
                    mRenderer.setPosition(0.0f,0.0f);
                    requestRender(); // Pour relancer le dessin
                    condition=false;
            }
        }*/

        return true;
    }

    /**
     * Utilise la fonction changeForme de mRenderer
     * @param forme la nouvelle forme
     * @param position la position de la forme
     */


    /**
     * Change la carte voulu (au-dessus ou en-dessous)
     * @param forme la nouvelle forme
     * @param position la position de la forme qui change (-1 en bas et 1 en haut)
     */
    public void changeForme(int forme, int position){
        mRenderer.changeForme(forme, position);
    }


}
