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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

/* MyGLRenderer implémente l'interface générique GLSurfaceView.Renderer */

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private Forme mFormeHaute;
    private Forme mFormeBasse;
    private Forme boutonPlus;
    private Forme boutonMoins;
    private Forme boutonEgale;
    private Forme boutonPasserTour;
    private Forme boutonAideCartes;
    private Forme boutonRegles;

    // Les matrices habituelles Model/View/Projection

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    private float[] formePosition = {0.0f, 0.0f};

    /* Première méthode équivalente à la fonction init en OpenGLSL */
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // la couleur du fond d'écran
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        /* on va définir une classe Square pour dessiner des carrés */
        mFormeHaute   = new Forme(-1, 1);
        mFormeBasse   = new Forme(-1, -1);
        boutonPlus = Forme.boutonsPlus();
        boutonMoins = Forme.boutonsMoins();
        boutonEgale = Forme.boutonsEgale();
        boutonPasserTour = Forme.boutonsPasserTour();
        boutonAideCartes = Forme.boutonsAideCartes();
        boutonRegles = Forme.boutonsRegles();
    }

    /* Deuxième méthode équivalente à la fonction Display */
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16]; // pour stocker une matrice

        // glClear rien de nouveau on vide le buffer de couleur et de profondeur */
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        /* on utilise une classe Matrix (similaire à glm) pour définir nos matrices P, V et M*/

        /*Si on souhaite positionner une caméra mais ici on n'en a pas besoin*/
       // Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
         /* Pour le moment on va utiliser une projection orthographique
           donc View = Identity
         */
        Matrix.setIdentityM(mViewMatrix,0);

        // Calculate the projection and view transformation*
        float ratio = 412f/915f;
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -16f, 16f, 1f, 7f);
        //Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        Matrix.setIdentityM(mModelMatrix,0);

        /* Pour définir une translation on donne les paramètres de la translation
        et la matrice (ici mModelMatrix) est multipliée par la translation correspondante
         */
        Matrix.translateM(mModelMatrix, 0, formePosition[0], formePosition[1], 0);

        Log.d("Renderer", "mSquarex"+Float.toString(formePosition[0]));
        Log.d("Renderer", "mSquarey"+Float.toString(formePosition[1]));

        /* scratch est la matrice PxVxM finale */
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mModelMatrix, 0);

        /* on appelle la méthode dessin du carré élémentaire */
        mFormeHaute.draw(scratch);
        mFormeBasse.draw(scratch);
        boutonPlus.draw(scratch);
        boutonMoins.draw(scratch);
        boutonEgale.draw(scratch);
        boutonPasserTour.draw(scratch);
        boutonAideCartes.draw(scratch);
        boutonRegles.draw(scratch);
        Log.d("Forme", "Forme dessinées");

    }

    /* équivalent au Reshape en OpenGLSL */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        /* ici on aurait pu se passer de cette méthode et déclarer
        la projection qu'à la création de la surface !!
         */
        GLES30.glViewport(0, 0, width, height);
        Matrix.orthoM(mProjectionMatrix, 0, -10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 1.0f);

    }

    /* La gestion des shaders ... */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        return shader;
    }


    /* Les méthodes nécessaires à la manipulation de la position finale du carré */
    public void setPosition(float x, float y) {
        /*mSquarePosition[0] += x;
        mSquarePosition[1] += y;*/
        formePosition[0] = x;
        formePosition[1] = y;

    }

    public float[] getPosition() {
        return formePosition;
    }

    /**
     * Change la carte voulu (au-dessus ou en-dessous)
     * @param forme la nouvelle forme
     * @param position la position de la forme qui change (-1 en bas et 1 en haut)
     */
    public void changeForme(int forme, int position){
        switch (position){
            case -1:
                mFormeBasse.changerForme(forme, position);
                break;

            case 1:
                mFormeHaute.changerForme(forme, position);
                break;

            default:
                Log.d("Forme", "Mauvais choix de position pour la forme");
                break;
        }
        Log.d("Forme", "Je dessine : " + forme + " à la position : " + position);
    }

    public Forme getBoutonPlus() {
        return boutonPlus;
    }

    public Forme getBoutonMoins() {
        return boutonMoins;
    }

    public Forme getBoutonEgale() {
        return boutonEgale;
    }

    public Forme getBoutonPasserTour() {
        return boutonPasserTour;
    }

    public Forme getBoutonAideCartes() {
        return boutonAideCartes;
    }

    public Forme getBoutonRegles() {
        return boutonRegles;
    }
}
