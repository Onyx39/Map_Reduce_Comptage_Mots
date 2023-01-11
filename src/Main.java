import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        // Enregistre le temps actuel 
        long start = System.currentTimeMillis();

        
        // Création d'un moniteur qui va gérer le fichier dont le chemin est spécifié en paramètre
        // On peut préciser le nombre de Mapper et de Reducer voulu (3 par défaut)
        // Ajouter le booléan true si vous voulez afficher le résultat dans la console
        Moniteur moniteur = new Moniteur ("src/data/bible.txt", 30, 2, true);
        // Démarre le travail du moniteur
        moniteur.demarrer();


        long end = System.currentTimeMillis();
        // Calcul du temps d'execution du programme
        long temps_execution = end - start;

        System.out.println("\nTemps d'execution du programme : " + temps_execution+ " ms\n");

    }
}