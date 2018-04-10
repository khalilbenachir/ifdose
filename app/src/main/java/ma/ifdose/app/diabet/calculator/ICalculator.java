package ma.ifdose.app.diabet.calculator;

import org.json.JSONException;

/**
 * Created by papyrus on 16/06/2017.
 */


public interface ICalculator {

    double totalGluco();
    double Ecart();
    double uc();
    double ur() throws JSONException;
    double uniteInjecter() throws JSONException;
}
