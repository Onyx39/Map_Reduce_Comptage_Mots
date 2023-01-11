import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Mapper extends Thread {

    private String texte_a_traiter;
    private HashMap<String, Integer> dictionnaire;
    private CountDownLatch compte_a_rebours;
    private static ArrayList<HashMap<String, ArrayList<Integer>>> resultats;

    public Mapper(String texte, CountDownLatch unCompteARebours, ArrayList<HashMap<String, ArrayList<Integer>>> res_mappers) {
        // On met tous les caractères en minuscules pour éviter de compter plusieurs fois le même mot
        texte_a_traiter = texte.toLowerCase();
        dictionnaire = new HashMap<String, Integer>();
        compte_a_rebours = unCompteARebours;
        resultats = res_mappers;
    }

    public void run () {
        // Le Mapper rempli son propre dictionnaire avec sa portion de texte
        remplirDictionnaire();
        Set<String> cles = dictionnaire.keySet();
        for (String cle : cles) {
            // On synchonise avec les autres Threads, car 'resultats' est partagé !
            synchronized(resultats) {
                // On hash le mot et on prend son modulo (nombre de Reducers) pour l'affecter à un Reducer
                int index = Math.abs(cle.hashCode()%resultats.size());
                // Si le mot est dejà connu, on ajoute un nombre d'occurrence à sa liste
                if (resultats.get(index).containsKey(cle)) {
                    resultats.get(index).get(cle).add(dictionnaire.get(cle));
                }
                else{
                    // Sinon on initialise avec une liste dans laquelle on insére son prmeier nombre d'occurrence
                    ArrayList<Integer> truc = new ArrayList<>();
                    truc.add(dictionnaire.get(cle));
                    resultats.get(index).put(cle, truc);
                };
            }

        }
        // Le Mapper a terminé son travail, on décrémente le compte à rebours
        synchronized(compte_a_rebours){compte_a_rebours.countDown();};
    }

    public void remplirDictionnaire () {
        // On sépare le texte en utilisant les espaces
        String[] mots = texte_a_traiter.split("\\s+");
        for (String mot : mots) {
            // Si le mot commence par un ou plusieurs tirets, on les retire
            while (mot.matches("-[\\p{L}\\p{M}\\-]+")) {
                mot = mot.substring(1);
            }
            
            // Si le mot fini par un ou plusieurs tirets, on les retire
            while (mot.matches("[\\p{L}\\p{M}\\-]+-")) {
                mot = mot.substring(0, mot.length() - 1);
            }

            // On choisit de ne traiter que les mots d'au moins 3 lettres
            if (mot.length() > 2 ) {
                if (dictionnaire.containsKey(mot)) {
                    // Si le mot a déjà été trouvé, on incrémente son nombre d'occurrence
                    Integer nouvelle_valeur = dictionnaire.get(mot) + 1;
                    dictionnaire.put(mot, nouvelle_valeur);
                }
            // Sinon on initialise son occurrence à 1
            else {dictionnaire.put(mot, 1);}
            }
        }
        //System.out.println(dictionnaire);
    }

    public String getTexte_a_traiter() {
        return texte_a_traiter;
    }

    public void setTexte_a_traiter(String texte_a_traiter) {
        this.texte_a_traiter = texte_a_traiter;
    }

    public HashMap<String, Integer> getDictionnaire() {
        return dictionnaire;
    }

    public void setDictionnaire(HashMap<String, Integer> dictionnaire) {
        this.dictionnaire = dictionnaire;
    }
    
}