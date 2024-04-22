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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.util.Log;

//import android.opengl.GLES20;
import android.opengl.GLES30;


//Dessiner un carré

public class Forme {
/* Le vertex shader avec la définition de gl_Position et les variables utiles au fragment shader
 */
    private final String vertexShaderCode =
        "#version 300 es\n"+
                "uniform mat4 uMVPMatrix;\n"+
            "in vec3 vPosition;\n" +
                "in vec4 vCouleur;\n"+
                "out vec4 Couleur;\n"+
                "out vec3 Position;\n"+
            "void main() {\n" +
                "Position = vPosition;\n"+
                "gl_Position = uMVPMatrix * vec4(vPosition,1.0);\n" +
                "Couleur = vCouleur;\n"+
            "}\n";

    private final String fragmentShaderCode =
            "#version 300 es\n"+
            "precision mediump float;\n" + // pour définir la taille d'un float
            "in vec4 Couleur;\n"+
            "in vec3 Position;\n"+
            "out vec4 fragColor;\n"+
            "void main() {\n" +
            "float x = Position.x;\n"+
            "float y = Position.y;\n"+
            "float test = x*x+y*y;\n"+
            /*"if (test>1.0) \n"+
                "discard;\n"+*/
            "fragColor = Couleur;\n" +
            "}\n";

    /* les déclarations pour l'équivalent des VBO */

    private final FloatBuffer vertexBuffer; // Pour le buffer des coordonnées des sommets du carré
    private final ShortBuffer indiceBuffer; // Pour le buffer des indices
    private final FloatBuffer colorBuffer; // Pour le buffer des couleurs des sommets

    /* les déclarations pour les shaders
    Identifiant du programme et pour les variables attribute ou uniform
     */
    private final int IdProgram; // identifiant du programme pour lier les shaders
    private int IdPosition; // idendifiant (location) pour transmettre les coordonnées au vertex shader
    private int IdCouleur; // identifiant (location) pour transmettre les couleurs
    private int IdMVPMatrix; // identifiant (location) pour transmettre la matrice PxVxM

    static final int COORDS_PER_VERTEX = 3; // nombre de coordonnées par vertex
    static final int COULEURS_PER_VERTEX = 4; // nombre de composantes couleur par vertex

    int []linkStatus = {0};

    /* Attention au repère au niveau écran (x est inversé)
     Le tableau des coordonnées des sommets
     Oui ce n'est pas joli avec 1.0 en dur ....
     */

    private short indices[];

    static float fourmisCoords[] = {
            -1.0f,   -1.0f, 0.0f, //0
            1.0f,  -1.0f, 0.0f, //1
            -1.0f,  1.0f, 0.0f, //2
            1.f,  1.f, 0.0f }; //3
    // Le tableau des couleurs
    static float fourmisColors[] = {
             1.0f,  0.0f, 0.0f, 1.0f, //0
             1.0f,  1.0f, 1.0f, 1.0f, //1
             0.0f,  1.0f, 0.0f, 1.0f, //2
             0.0f,  0.0f, 1.0f, 1.0f }; //3

    // Le carré est dessiné avec 2 triangles

    static float escargotCoords[] = {
            -2.f,   -2f, 0.0f, //0
            2.f,  -2.0f, 0.0f, //1
            0.0f,  1.0f, 0.0f}; //2
    // Le tableau des couleurs
    static float escargotColors[] = {
            1.0f, 0.0f, 0.0f, 1.0f, //0
            1.0f, 1.0f, 1.0f, 1.0f, //1
            0.0f, 1.0f, 0.0f, 1.0f}; //2

    static float grenouilleCoords[] = {
            -1.5f,   -1.125f, 0.0f, //0
            1.5f,  -1.125f, 0.0f, //1
            -3.5f,  1.125f, 0.0f, //2
            3.5f, 1.125f, 0.0f, //3
            0.0f, 1.125f, 0.0f}; //4

    static float grenouilleColors[] = {
            1.0f, 0.0f, 0.0f, 1.0f, //0
            1.0f, 1.0f, 1.0f, 1.0f, //1
            1.0f, 1.0f, 1.0f, 1.0f, //2
            1.0f, 1.0f, 1.0f, 1.0f, //3
            0.0f, 1.0f, 0.0f, 1.0f}; //4

    static float herissonCoords[] = {
            -2.5f, -1.8f, 0.0f, //0
            2.5f,  -1.8f, 0.0f, //1
            -4.5f,  0.45f, 0.0f, //2
            4.5f, 0.45f, 0.0f, //3
            0.0f, 1.35f, 0.0f, //4
            0.0f, 1.f, 0.0f}; //5

    static float herissonColors[] = {
            1.0f, 0.0f, 0.0f, 1.0f, //0
            1.0f, 1.0f, 1.0f, 1.0f, //1
            1.0f, 1.0f, 1.0f, 1.0f, //2
            1.0f, 1.0f, 1.0f, 1.0f, //3
            1.0f, 1.0f, 1.0f, 1.0f, //4
            0.0f, 1.0f, 0.0f, 1.0f}; //5

    static float renardCoords[] = {
            -5f, -2.25f, 0.0f, //0
            5f,  -2.25f, 0.0f, //1
            -3f,  2.25f, 0.0f, //2
            7f, 2.25f, 0.0f}; //3

    static float renardColors[] = {
            1.0f, 0.0f, 0.0f, 1.0f, //0
            1.0f, 1.0f, 1.0f, 1.0f, //1
            1.0f, 1.0f, 1.0f, 1.0f, //2
            0.0f, 1.0f, 0.0f, 1.0f}; //3

    static float bicheCoords[] = {
            -2.5f, -2.93f, 0.0f, //0
            2.5f,  -2.93f, 0.0f, //1
            -5.5f, -1.58f, 0.0f, //2
            5.5f,  -1.58f, 0.0f, //3
            -5.5f, 1.58f, 0.0f, //4
            5.5f,  1.58f, 0.0f, //5
            -2.5f, 2.93f, 0.0f, //6
            2.5f,  2.93f, 0.0f, //7
            -2.5f, 1.58f, 0.0f, //8
            2.5f, 1.58f, 0.0f, //9
            -2.5f, 1.58f, 0.0f, //10
            2.5f, 1.58f, 0.0f}; //11

    static float bicheColors[] = {
            1.0f, 0.0f, 0.0f, 1.0f, //0
            1.0f, 1.0f, 1.0f, 1.0f, //1
            1.0f, 1.0f, 1.0f, 1.0f, //2
            1.0f, 0.0f, 0.0f, 1.0f, //3
            1.0f, 1.0f, 1.0f, 1.0f, //4
            1.0f, 1.0f, 1.0f, 1.0f, //5
            0.0f, 1.0f, 0.0f, 1.0f, //6
            1.0f, 1.0f, 1.0f, 1.0f, //7
            1.0f, 1.0f, 1.0f, 1.0f, //8
            0.0f, 1.0f, 0.0f, 1.0f, //9
            0.0f, 1.0f, 0.0f, 1.0f, //10
            0.0f, 1.0f, 0.0f, 1.0f}; //11

    static float oursCoords[] = {
            -2.5f, -3.38f, 0.0f, //0
            2.5f,  -3.38f, 0.0f, //1
            -5.5f, -2.03f, 0.0f, //2
            -2.5f, 2.03f, 0.0f, //3
            2.5f, 2.03f, 0.0f, //4
            5.5f,  -2.03f, 0.0f, //5
            -5.5f, 2.03f, 0.0f, //6
            -2.5f, 2.03f, 0.0f, //7
            2.5f, 2.03f, 0.0f, //8
            5.5f,  2.03f, 0.0f, //9
            -2.5f, 3.38f, 0.0f, //10
            2.5f,  3.38f, 0.0f}; //11

    static float oursColors[] = {
            1.0f, 0.0f, 0.0f, 1.0f, //0
            1.0f, 1.0f, 1.0f, 1.0f, //1
            1.0f, 1.0f, 1.0f, 1.0f, //2
            1.0f, 0.0f, 0.0f, 1.0f, //3
            1.0f, 1.0f, 1.0f, 1.0f, //4
            1.0f, 1.0f, 1.0f, 1.0f, //5
            0.0f, 1.0f, 0.0f, 1.0f, //6
            1.0f, 0.0f, 0.0f, 1.0f, //7
            1.0f, 1.0f, 1.0f, 1.0f, //8
            1.0f, 1.0f, 1.0f, 1.0f, //9
            0.0f, 1.0f, 0.0f, 1.0f, //10
            0.0f, 1.0f, 0.0f, 1.0f}; //11

    private final int vertexStride = COORDS_PER_VERTEX * 4; // le pas entre 2 sommets : 4 bytes per vertex

    private final int couleurStride = COULEURS_PER_VERTEX * 4; // le pas entre 2 couleurs

    private final float Position[] = {0.0f,0.0f};

    public Forme(int forme, float[] Pos) {
        float coords[];
        float couleurs[];
        switch (forme){
            case 0:
                coords = fourmisCoords;
                couleurs = fourmisColors;
                indices = new short[]{
                        0, 2, 3, //0
                        0, 1, 3}; //1
                break;

            case 1:
                coords = escargotCoords;
                couleurs = escargotColors;
                indices = new short[]{
                        0, 1, 2}; //0
                break;

            case 2:
                coords = grenouilleCoords;
                couleurs = grenouilleColors;
                indices = new short[]{
                        0, 2, 4, //0
                        0, 1, 4, //1
                        1, 4, 3}; //2
                break;

            case 3:
                coords = herissonCoords;
                couleurs = herissonColors;
                indices = new short[]{
                        0, 2, 5, //0
                        0, 1, 5, //1
                        1, 5, 3, //2
                        2, 3, 4}; //3
                break;

            case 4:
                coords = renardCoords;
                couleurs = renardColors;
                indices = new short[]{
                        0, 1, 2, //0
                        1, 2, 3}; //1
                break;

            case 5:
                coords = bicheCoords;
                couleurs = bicheColors;
                indices = new short[]{
                        0, 2, 8, //0
                        0, 1, 8, //1
                        1, 8, 9, //2
                        1, 9, 3, //3
                        2, 3, 4, //4
                        3, 4, 5, //5
                        4, 10, 6, //6
                        10, 11, 6, //7
                        11, 6, 7, //8
                        11, 5, 7}; //9
                break;

            case 6:
                coords = oursCoords;
                couleurs = oursColors;
                indices = new short[]{
                        0, 1, 3, //0
                        1, 3, 4, //1
                        2, 5, 6, //2
                        5, 6, 9, //3
                        7, 8, 10, //4
                        8, 10, 11}; //5
                break;

            default:
                coords = new float[0];
                couleurs = new float[0];
                indices = new short[0];
                break;

        }
        Position[0] = Pos[0];
        Position[1] = Pos[1];
        // initialisation du buffer pour les vertex (4 bytes par float)
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);


        // initialisation du buffer pour les couleurs (4 bytes par float)
        ByteBuffer bc = ByteBuffer.allocateDirect(couleurs.length * 4);
        bc.order(ByteOrder.nativeOrder());
        colorBuffer = bc.asFloatBuffer();
        colorBuffer.put(couleurs);
        colorBuffer.position(0);

        // initialisation du buffer des indices
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        indiceBuffer = dlb.asShortBuffer();
        indiceBuffer.put(indices);
        indiceBuffer.position(0);

        /* Chargement des shaders */
        int vertexShader = MyGLRenderer.loadShader(
                GLES30.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES30.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        IdProgram = GLES30.glCreateProgram();             // create empty OpenGL Program
        GLES30.glAttachShader(IdProgram, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(IdProgram, fragmentShader); // add the fragment shader to program
        GLES30.glLinkProgram(IdProgram);                  // create OpenGL program executables
        GLES30.glGetProgramiv(IdProgram, GLES30.GL_LINK_STATUS,linkStatus,0);


    }



    public void set_position(float[] pos) {
        Position[0]=pos[0];
        Position[1]=pos[1];
    }
    /* La fonction Display */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES30.glUseProgram(IdProgram);

           // get handle to shape's transformation matrix
        IdMVPMatrix = GLES30.glGetUniformLocation(IdProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(IdMVPMatrix, 1, false, mvpMatrix, 0);


        // get handle to vertex shader's vPosition member et vCouleur member
        IdPosition = GLES30.glGetAttribLocation(IdProgram, "vPosition");
        IdCouleur = GLES30.glGetAttribLocation(IdProgram, "vCouleur");

        /* Activation des Buffers */
        GLES30.glEnableVertexAttribArray(IdPosition);
        GLES30.glEnableVertexAttribArray(IdCouleur);

        /* Lecture des Buffers */
        GLES30.glVertexAttribPointer(
                IdPosition, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        GLES30.glVertexAttribPointer(
                IdCouleur, COULEURS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                couleurStride, colorBuffer);


        // Draw the square
        GLES30.glDrawElements(
                GLES30.GL_TRIANGLES, indices.length,
                GLES30.GL_UNSIGNED_SHORT, indiceBuffer);


        // Disable vertex array
        GLES30.glDisableVertexAttribArray(IdPosition);
        GLES30.glDisableVertexAttribArray(IdCouleur);

    }

}
