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
        texte_a_traiter = texte.toLowerCase();
        dictionnaire = new HashMap<String, Integer>();
        compte_a_rebours = unCompteARebours;
        resultats = res_mappers;
    }

    public void run () {
        remplirDictionnaire();
        Set<String> cles = dictionnaire.keySet();
        for (String cle : cles) {
            synchronized(resultats) {
                int index = Math.abs(cle.hashCode()%resultats.size());
                if (resultats.get(index).containsKey(cle)) {
                    resultats.get(index).get(cle).add(dictionnaire.get(cle));
                }
                else{
                    ArrayList<Integer> truc = new ArrayList<>();
                    truc.add(dictionnaire.get(cle));
                    resultats.get(index).put(cle, truc);
                };
            }

        }
        compte_a_rebours.countDown();
    }

    public void remplirDictionnaire () {
        String[] mots = texte_a_traiter.split("\\s+");
        for (String mot : mots) {
            while (mot.matches("-[\\p{L}\\p{M}\\-]+")) {
                mot = mot.substring(1);
            }
            
            while (mot.matches("[\\p{L}\\p{M}\\-]+-")) {
                mot = mot.substring(0, mot.length() - 1);
            }
            if (mot.length() > 2 ) {
                if (dictionnaire.containsKey(mot)) {
                    Integer nouvelle_valeur = dictionnaire.get(mot) + 1;
                    dictionnaire.put(mot, nouvelle_valeur);
                }
            else {dictionnaire.put(mot, 1);}
            }
        }
        System.out.println(dictionnaire);
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