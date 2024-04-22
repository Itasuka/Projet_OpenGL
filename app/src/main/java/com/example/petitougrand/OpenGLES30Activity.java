package com.example.petitougrand;



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

    List<List<Integer>> playerCards;
    LinkedList<Integer> queuePlayer;
    int cardInGame;
    int nombreDeJoueurs;
    int joueurActuel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonCarte = findViewById(R.id.buttonCarte);
        Button buttonAide = findViewById(R.id.buttonAide);
        Button buttonMoins = findViewById(R.id.buttonMoins);
        Button buttonEgal = findViewById(R.id.buttonEgal);
        Button buttonPlus = findViewById(R.id.buttonPlus);
        Button buttonStop = findViewById(R.id.buttonStop);

        newGame();

        buttonCarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCardPopup();
            }
        });

        buttonAide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpPopup();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTurn();
            }
        });

        buttonMoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void newGame(){
        showPlayersPopup();
    }

    private void setCards(){
        joueurActuel = 0;
        Random random = new Random();
        int nombreAleatoire = random.nextInt(7);
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
                    Log.d("PlayerCardsmod", String.valueOf(i%nombreDeJoueurs));
                    playerCards.get(i%nombreDeJoueurs).add(nombreAleatoire);
                    gameCards.set(nombreAleatoire,gameCards.get(nombreAleatoire)-1);
                    break;
                }
            }
        }
        setIntefaceForPlayer();
    }

    private void setIntefaceForPlayer(){
        TextView joueur = findViewById(R.id.textJoueur);
        TextView score = findViewById(R.id.textScore);
        score.setText(String.valueOf(playerCards.get(joueurActuel).size()));
        joueur.setText("J"+String.valueOf(joueurActuel+1));
    }

    private void endTurn(){
        if(queuePlayer != null && !queuePlayer.isEmpty())
            cardInGame = queuePlayer.getLast();
        joueurActuel = (joueurActuel+1)%nombreDeJoueurs;
        setIntefaceForPlayer();
    }

    private void showCardPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_layout);
        dialog.show();
    }

    private void showHelpPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_card);
        dialog.show();
    }

    void showPlayersPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_players);

        dialog.setCanceledOnTouchOutside(false);

        Spinner spinnerNumbers = dialog.findViewById(R.id.spinnerJoueurs);
        String[] numbers = {"2", "3", "4"};
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

    private void displayPlayerCards() {
        // Map pour stocker le cumul de chaque entier
        Map<Integer, Integer> cumul = new HashMap<>();

        // Parcourir toutes les listes de cartes des joueurs
        for (List<Integer> cards : playerCards) {
            // Parcourir toutes les cartes dans la liste
            for (int card : cards) {
                // Mettre à jour le cumul de la carte dans la Map
                cumul.put(card, cumul.getOrDefault(card, 0) + 1);
            }
        }

        // Afficher le cumul de chaque entier dans les listes de cartes des joueurs
        for (Map.Entry<Integer, Integer> entry : cumul.entrySet()) {
            int card = entry.getKey();
            int count = entry.getValue();
            Log.d("PlayerCards", "Cumul de " + card + " : " + count);
        }
    }
}
