import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Moniteur {

    private String chemin;
    private int nbMapper;
    private int nbReducer;
    private boolean affichageResultat;


    public Moniteur(String unChemin) {
        chemin = unChemin;
        nbMapper = 3;
        nbReducer = 3;
        affichageResultat = false;
    }

    public Moniteur(String unChemin, boolean unBool) {
        chemin = unChemin;
        nbMapper = 3;
        nbReducer = 3;
        affichageResultat = unBool;
    }

    public Moniteur(String unChemin, int unNbMapper, int unNbReducer) {
        chemin = unChemin;
        nbMapper = unNbMapper;
        nbReducer = unNbReducer;
        affichageResultat = false;
    }

    public Moniteur(String unChemin, int unNbMapper, int unNbReducer, boolean unBool) {
        chemin = unChemin;
        nbMapper = unNbMapper;
        nbReducer = unNbReducer;
        affichageResultat = unBool;
    }

    public void demarrer () throws IOException, InterruptedException {

        // Lecture du fichier
        FileReader fichier = new FileReader(chemin);
        BufferedReader buffer = new BufferedReader(fichier);

        // Liste qui stocke les sous-textes qui seront donnés aux Mappers
        ArrayList<String> liste_sous_textes = new ArrayList<String>();

        // Boucle qui permet d'initialiser la liste en fonction du nombre de Mappers
        for (int j = 0; j < nbMapper; j++) {
            liste_sous_textes.add("");
        } 

        // Permet de compter le nombre de boucle déjà réalisé
        // Compteur qui servira pour le modulo
        int compteur = 0;
        String ligne;
        // Remplissage de la liste
        while ((ligne = buffer.readLine()) != null) {
            // On remplace tous les signes qui ne sont pas des lettres ou des tirets par des espaces
            ligne = ligne.replaceAll("[^\\p{L}\\p{M}\\-]+", " "); 
            // Pour répartir les lignes, on utilise un modulo
            // On estime que la répartiton entre les Mappers sera à peu près égale 
            String nouvelle_valeur = liste_sous_textes.get(compteur%nbMapper) + " " + ligne;
            liste_sous_textes.set(compteur%nbMapper, nouvelle_valeur);
            compteur++;
        }

        // Fermeture du buffer
        buffer.close();

        // Création d'un dictionnaire qui contiendra les résultats des Mapper
        // A chaque mot est associé une liste, dans laquelle chaque Mapper ajoute le nombre d'occurrence de ce mot dans sa partie de texte
        ArrayList<HashMap<String, ArrayList<Integer>>> res_mappers = new ArrayList<>();
        // Initialisation en fonction de leur nombre
        for (int j = 0; j < nbReducer; j++) {
            res_mappers.add(new HashMap<>());
        } 
        // Création d'un compte à rebours pour attendre que tous les Mappers aient terminé
        CountDownLatch compte_a_rebours = new CountDownLatch(nbMapper);
        // Lancement des Mappers dans différents Threads
        for (int i = 0; i < nbMapper; i++) {
            Mapper test = new Mapper(liste_sous_textes.get(i), compte_a_rebours, res_mappers);
            test.start();
        }
        // Synchronisation : on attend que tous les Threads aient terminé
        compte_a_rebours.await();


        // // Création d'un dictionnaire qui contiendra les résultats des Reducer (le résultat final)
        HashMap<String, Integer> res_reducer = new HashMap<String, Integer>();
        // Création d'un compte à rebours pour attendre que tous les Reducer aient terminé
        CountDownLatch compte_a_rebours_2 = new CountDownLatch(nbReducer);
        // Lancement des Reducers dans différents Threads
        for (int j = 0; j < nbReducer; j++) {
            Reducer test = new Reducer(res_mappers.get(j), compte_a_rebours_2, res_reducer);
            test.start();
        }
        // Synchronisation : on attend que tous les Threads aient terminé
        compte_a_rebours_2.await();

        // Permet d'afficher le dictionnaire final trié dasn l'ordre alphabétique
        // Attention : méthode lourde pour les grands fichiers
        if (affichageResultat) {
        System.out.println("\n\nRésultat du programme : \n" + sortHashMap(res_reducer)
            + "\n\nNombre de mots comptés : " + compteMotTotal(res_reducer));
        }

    }

    // Méthode pour trier une HashMap par l'ordre alphabétique de ses clés.
    public LinkedHashMap<String, Integer> sortHashMap (HashMap<String, Integer> hm) {
        LinkedHashMap<String, Integer> sortedHM = new LinkedHashMap<>();
        hm.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEachOrdered(x -> sortedHM.put(x.getKey(), x.getValue()));
        return sortedHM;
    }

    // Méthode qui compte le nombre de mots traité par le programme
    public int compteMotTotal (HashMap<String, Integer> hm) {
        int compteur = 0;
        Set<String> cles = hm.keySet();
        for (String cle : cles) {
            compteur += hm.get(cle);
        }
        return compteur;
    }

}