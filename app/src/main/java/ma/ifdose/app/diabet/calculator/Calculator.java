package ma.ifdose.app.diabet.calculator;

import java.util.ArrayList;
import java.util.HashMap;

import ma.ifdose.app.Models.Aliment;
import timber.log.Timber;

public class Calculator implements ICalculator {

    protected ArrayList<Aliment> aliments;
    protected HashMap<Integer, Double> selectedAliments;
    protected double glucoAvantRepas;
    protected HashMap<Integer, HashMap<String, Double>> alimentsPrepared;
    private double rm, rd, rc, rdi, is, obj;
    private int ratio;
    private double glucoTotal;
    private boolean avecLivret = true;

    public Calculator(ArrayList aliments, double rm, double rd, double rc, double rdi,
                      double is, double obj, double glucoAvantRepas, int ratio) {
        this.aliments = aliments;
        this.rm = rm;
        this.rd = rd;
        this.rc = rc;
        this.is = is;
        this.obj = obj;
        this.rdi = rdi;
        this.glucoAvantRepas = glucoAvantRepas;
        this.ratio = ratio;
        avecLivret = true;
    }

    public Calculator(double glucoTotal, double rm, double rd, double rc, double rdi,
                      double is, double obj, double glucoAvantRepas, int ratio) {
        this.glucoTotal = glucoTotal;
        this.rm = rm;
        this.rd = rd;
        this.rc = rc;
        this.is = is;
        this.obj = obj;
        this.rdi = rdi;
        this.glucoAvantRepas = glucoAvantRepas;
        this.ratio = ratio;
        avecLivret = false;
    }

    @Override
    public double totalGluco() {
        // TODO : get total gluco
        // sum = 0
        //while ( selectedAliment)
        // Get gluco of one aliment (alimentGluco)
        // sum += alimentGluco
        //endwhile

        try {
            if (avecLivret) {
                double resultat = 0;

                for (int j = 0; j < aliments.size(); j++) {
                    double gb = aliments.get(j).getGlucide();
                    if (gb != 0) {
                        String qa = aliments.get(j).getQuantiteA();
                        String[] parts = qa.split(" ");
                        double qaS = Double.parseDouble(parts[0]);
                        String uniteS = parts[1];
                        double qb = aliments.get(j).getQuantiteB();
                        Timber.i("qB : " + qb);
                        if (uniteS.equals("قطعة") || uniteS.equals("طبق") || uniteS.equals("كاس")) {
                            qb = 1;
                        }
                        resultat += qaS * gb / qb;
                    }//return sum
                }
                return resultat;
            } else {
                return glucoTotal;
            }
        } catch (Exception e) {
            Timber.e("Exception : " + e.getMessage());
            return 1;
        }
    }


    @Override
    public double Ecart() {
        //TODO : gAvantRepas-objectif
        double objectif = obj;//get from user object;
        return glucoAvantRepas - objectif;
    }

    /**
     * Unité de Correction
     */
    @Override
    public double uc() {
        // TODO : ecart/sensibilité
        double sensibilite = is;
        return Ecart() / sensibilite;
    }

    /**
     * Unité de repas
     */
    @Override
    public double ur() {
        return totalGluco() * getRatio() / 10;
    }

    @Override
    public double uniteInjecter() {

        if (glucoAvantRepas > 1.2 || glucoAvantRepas < 0.7)
            return uc() + ur();

        //TODO : UC + UR
        return ur();
    }

    public double getRatio() {
        double r = 0;
        switch (ratio) {
            case 0:
                r = rm;
                break;
            case 1: // Ratio dej
                r = rd;
                break;
            case 2: // Ratio collation
                r = rc;
                break;
            case 3: // Ratio diner
                r = rdi;
                break;
        }
        return r;
    }

}


