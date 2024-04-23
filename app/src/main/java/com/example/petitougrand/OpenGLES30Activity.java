package com.example.petitougrand;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/* Ce tutorial est issu d'un tutorial http://developer.android.com/training/graphics/opengl/index.html :
openGLES.zip HelloOpenGLES20
 */


public class OpenGLES30Activity extends AppCompatActivity {
    final int FOURMI = 8;
    final int ESCARGOT = 9;
    final int GRENOUILLE = 9;
    final int HERISSON = 9;
    final int RENARD = 9;
    final int BICHE = 9;
    final int OURS = 8;

    MyGLSurfaceView myGLSurfaceView;
    List<LinkedList<Integer>> playerCards;
    LinkedList<Integer> queuePlayer;
    int cardInGame;
    int nombreDeJoueurs;
    int joueurActuel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myGLSurfaceView = findViewById(R.id.myGLSurfaceView);
        myGLSurfaceView.setApplication(this);

        newGame();
    }

    private void newGame(){
        showPlayersPopup();
    }

    private void setCards(){
        joueurActuel = 0;
        Random random = new Random();
        int nombreAleatoire = random.nextInt(7);
        queuePlayer = new LinkedList<>();
        List<Integer> gameCards = new ArrayList<>();
        gameCards.add(FOURMI);
        gameCards.add(ESCARGOT);
        gameCards.add(GRENOUILLE);
        gameCards.add(HERISSON);
        gameCards.add(RENARD);
        gameCards.add(BICHE);
        gameCards.add(OURS);

        playerCards = new ArrayList<>();
        for (int i = 0; i < nombreDeJoueurs; i++) {
            playerCards.add(new LinkedList<>());
        }

        cardInGame = nombreAleatoire;
        gameCards.set(nombreAleatoire,gameCards.get(nombreAleatoire)-1);

        for (int i = 0; i < 60; i++) {
            while(true){
                nombreAleatoire = random.nextInt(7);
                Log.d("PlayerCards", String.valueOf(nombreAleatoire));

                if(gameCards.get(nombreAleatoire)!=0){
                    playerCards.get(i%nombreDeJoueurs).add(nombreAleatoire);
                    gameCards.set(nombreAleatoire,gameCards.get(nombreAleatoire)-1);
                    break;
                }
            }
        }
        setIntefaceForPlayer();
    }

    private void setIntefaceForPlayer(){
        myGLSurfaceView = findViewById(R.id.myGLSurfaceView);
        myGLSurfaceView.changeForme(cardInGame,1);
        if(queuePlayer.isEmpty())
            myGLSurfaceView.changeForme(-1,-1);
        else
            myGLSurfaceView.changeForme(queuePlayer.getLast(), -1);
        myGLSurfaceView.requestRender();
        TextView joueur = findViewById(R.id.textJoueur);
        TextView score = findViewById(R.id.textScore);
        score.setText(String.valueOf(playerCards.get(joueurActuel).size()));
        joueur.setText("J"+String.valueOf(joueurActuel+1));
        if (playerCards.get(joueurActuel).size() == 0){
            showReplayPopup();
        }
        displayPlayerCards();
    }

    public void endTurn(){
        joueurActuel = (joueurActuel+1)%nombreDeJoueurs;
        String message = "Vous vous êtes trompé!";
        if(queuePlayer != null && !queuePlayer.isEmpty())
            message = "Vous passez votre tour!";
        showNextPlayerPopup(new PopupClosedListener() {
            @Override
            public void onPopupClosed() {
                if(queuePlayer != null && !queuePlayer.isEmpty()) {
                    cardInGame = queuePlayer.getLast();
                    queuePlayer = new LinkedList<>();
                }
                setIntefaceForPlayer();
            }
        }, message);
    }

    public void playLess(){
        queuePlayer.add(playerCards.get(joueurActuel).pop());
        setIntefaceForPlayer();
        if(queuePlayer.getLast() >= cardInGame){
            for (Integer i: queuePlayer) {
                playerCards.get(joueurActuel).addLast(i);
            }
            queuePlayer = new LinkedList<>();
            endTurn();
        }
    }

    public void playMore(){
        queuePlayer.add(playerCards.get(joueurActuel).pop());
        setIntefaceForPlayer();
        if(queuePlayer.getLast() <= cardInGame){
            for (Integer i: queuePlayer) {
                playerCards.get(joueurActuel).addLast(i);
            }
            queuePlayer = new LinkedList<>();
            endTurn();
        }
    }

    public void playEqual(){
        queuePlayer.add(playerCards.get(joueurActuel).pop());
        setIntefaceForPlayer();
        if(queuePlayer.getLast() != cardInGame){
            for (Integer i: queuePlayer) {
                playerCards.get(joueurActuel).addLast(i);
            }
            queuePlayer = new LinkedList<>();
            endTurn();
        }
    }

    public void showCardPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_layout);
        dialog.show();
    }

    public void showHelpPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_card);
        dialog.show();
    }

    public void showPlayersPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_players);

        dialog.setCanceledOnTouchOutside(false);

        Spinner spinnerNumbers = dialog.findViewById(R.id.spinnerJoueurs);
        String[] numbers = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumbers.setAdapter(adapter);

        Button buttonOK = dialog.findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedValue = spinnerNumbers.getSelectedItem().toString();
                nombreDeJoueurs = Integer.parseInt(selectedValue);
                dialog.dismiss();
                setCards();
            }
        });

        dialog.show();
    }

    public void showReplayPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Voulez-vous rejouer ?");
        builder.setMessage("Le joueur " + (joueurActuel+1) + " a gagné");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newGame();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showNextPlayerPopup(PopupClosedListener listener, String message){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_nextplayer);
        TextView player = dialog.findViewById(R.id.joueur);
        TextView reason = dialog.findViewById(R.id.raison);
        player.setText(String.valueOf(joueurActuel+1));
        reason.setText(message);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listener.onPopupClosed();
            }
        });
        dialog.show();
    }

    private void displayPlayerCards() {
        Log.d("PlayerCards", "Cartes en jeu: " + cardInGame);
        for (int i = 0; i < playerCards.size(); i++) {
            List<Integer> cards = playerCards.get(i);
            Log.d("PlayerCards", "Cartes du joueur " + i + ": " + cards.toString());
        }
    }
}
